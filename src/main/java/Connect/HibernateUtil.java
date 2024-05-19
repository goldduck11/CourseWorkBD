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
