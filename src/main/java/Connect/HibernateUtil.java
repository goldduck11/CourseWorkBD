package Connect;

import jakarta.persistence.TypedQuery;
import org.example.Entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import Enum.RequisitionStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private Session session;

    public HibernateUtil() {
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        session = sessionFactory.openSession();
    }

    public Map<Product, ProductInfo> getQuantityProduct() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            String hql = "SELECT p, SUM(sp.quantity), p.price " +
                    "FROM StorageProduct sp " +
                    "JOIN Product p ON sp.productId = p.id " +
                    "GROUP BY p.id, p.name, p.price";
            TypedQuery<Object[]> query = session.createQuery(hql, Object[].class);
            List<Object[]> results = query.getResultList();

            Map<Product, ProductInfo> productQuantities = new HashMap<>();

            for (Object[] result : results) {
                Product product = (Product) result[0];
                Long totalQuantity = (Long) result[1];
                Double price = (Double) result[2];

                productQuantities.put(product, new ProductInfo(totalQuantity, price));
            }

            for (Map.Entry<Product, ProductInfo> entry : productQuantities.entrySet()) {
                System.out.println("Product: " + entry.getKey().getName() +
                        ", Total Quantity: " + entry.getValue().getQuantity() +
                        ", Price: " + entry.getValue().getPrice());
            }
            session.getTransaction().commit();
            return productQuantities;
        }
    }

    public void createRequisitionFromCart(List<ProductCert> productCerts, User user, double totalPrice) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Requisition requisition = new Requisition();
            requisition.setDate(new Date());
            requisition.setStatus(RequisitionStatus.NEW.getString());
            requisition.setUser(user);

            session.save(requisition);

            Long requisitionId = requisition.getId();

            for (ProductCert productCert : productCerts) {
                // Проверка текущего количества товара на складе
                String hql = String.format("SELECT sp FROM StorageProduct sp WHERE sp.productId = %s", productCert.getProduct().getId());
                TypedQuery<Object[]> query = session.createQuery(hql, Object[].class);
                List<Object[]> results = query.getResultList();

                if (results == null) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Product not found in storage: " + productCert.getProduct().getName());
                }

                StorageProduct storageProduct = ((StorageProduct)results.get(0)[0]);
                long currentQuantity = storageProduct.getQuantity();
                long requestedQuantity = productCert.getProductInfo().getQuantity();

                if (requestedQuantity > currentQuantity) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Not enough stock for product: " + productCert.getProduct().getName());
                }

                // Вычитание количества товара из базы данных
                long newQuantity = currentQuantity - requestedQuantity;
                storageProduct.setQuantity(newQuantity);
                session.update(storageProduct);

                RequisitionProduct requisitionProduct = new RequisitionProduct();
                requisitionProduct.setRequisitionId(requisitionId);
                requisitionProduct.setProductId(productCert.getProduct().getId());
                requisitionProduct.setQuantity(productCert.getProductInfo().getQuantity());
                requisitionProduct.setPrice(totalPrice);

                session.save(requisitionProduct); // Сохранение товара заявки в базе данных

            }

            session.getTransaction().commit();
        }
    }

    public void cancelRequisition(Long requisitionId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // Получение заявки
            Requisition requisition = session.get(Requisition.class, requisitionId);
            if (requisition == null) {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Requisition not found: " + requisitionId);
            }

            // Получение списка товаров в заявке
            String hql = "FROM RequisitionProduct rp WHERE rp.requisitionId = :requisitionId";
            List<RequisitionProduct> requisitionProducts = session.createQuery(hql, RequisitionProduct.class)
                    .setParameter("requisitionId", requisitionId)
                    .getResultList();

            // Возврат количества товаров на склад
            for (RequisitionProduct requisitionProduct : requisitionProducts) {
                String productHql = "FROM StorageProduct sp WHERE sp.productId = :productId";
                StorageProduct storageProduct = session.createQuery(productHql, StorageProduct.class)
                        .setParameter("productId", requisitionProduct.getProductId())
                        .uniqueResult();

                if (storageProduct == null) {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Product not found in storage: " + requisitionProduct.getProductId());
                }

                long newQuantity = storageProduct.getQuantity() + requisitionProduct.getQuantity();
                storageProduct.setQuantity(newQuantity);
                session.update(storageProduct);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        }
        return sessionFactory;
    }

    public static Session getSession() {
        Session session = getSessionFactory().openSession();
        // Начало транзакции
        session.beginTransaction();
        return session;
    }

}
