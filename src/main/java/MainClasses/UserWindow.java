package MainClasses;

import Connect.HibernateUtil;
import org.example.Entity.Product;
import org.example.Entity.ProductCert;
import org.example.Entity.ProductInfo;
import org.example.Entity.User;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserWindow extends JFrame {
    private JPanel panel1;
    private JButton button;
    private JList<ProductCert> cart;
    private DefaultListModel<ProductCert> cartModel;
    private JList<ProductCert> availability;
    private JTextField total;
    private JLabel login;
    private User user;

    private HibernateUtil hibernateUtil = new HibernateUtil();

    public UserWindow(User user) {
        panel1.setBorder(BorderFactory.createEmptyBorder(50, 10, 50, 10));
        this.login.setText(user.getLogin());
        this.user = user;
        this.setContentPane(panel1);
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        initButton();
        initAvailability();
    }

    private void initButton() {
        button.addActionListener(e -> {
            DefaultListModel<ProductCert> cartModel = (DefaultListModel<ProductCert>) cart.getModel();
            List<ProductCert> productList = new ArrayList<>();
            for (int i = 0; i < cartModel.getSize(); i++) {
                ProductCert productCert = cartModel.getElementAt(i);
                productList.add(productCert);
            }
            cartModel.clear();
            cart.setModel(cartModel);
            DecimalFormat df = new DecimalFormat("#.##");
            hibernateUtil.createRequisitionFromCart(productList, user, Double.parseDouble(total.getText().replace(",", ".")));
        });
    }

    private void initAvailability() {
        Map<Product, ProductInfo> productQuantities = hibernateUtil.getQuantityProduct();
        DefaultListModel<ProductCert> productModel = new DefaultListModel<>();
        for (Map.Entry<Product, ProductInfo> entry : productQuantities.entrySet()) {
            ProductCert productCert = new ProductCert(entry.getKey(), entry.getValue());
            productModel.addElement(productCert);
        }
        availability.setModel(productModel);

        availability.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Проверяем, что был двойной клик
                if (e.getClickCount() == 2) {
                    // Получаем выбранный продукт
                    ProductCert selectedProduct = availability.getSelectedValue();
                    if (selectedProduct != null) {
                        // Открываем диалоговое окно для ввода количества товара
                        String input = JOptionPane.showInputDialog("Enter quantity for " + selectedProduct.getProduct().getName() + ":");
                        if (input != null && !input.isEmpty()
                                && Long.decode(input).compareTo(selectedProduct.getProductInfo().getQuantity()) <= 0) {
                            try {
                                // Преобразуем введенное значение в число
                                Long quantity = Long.decode(input);
                                // Добавляем товар в корзину с указанным количеством
                                addProductToCart(selectedProduct, quantity);
                            } catch (NumberFormatException ex) {
                                // Если введено некорректное значение
                                JOptionPane.showMessageDialog(null, "Invalid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Некорректное количество!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void addProductToCart(ProductCert product, Long quantity) {
        if (cartModel == null) {
            cartModel = new DefaultListModel<>();
            cart.setModel(cartModel);
        }

        // Проверяем, есть ли уже такой товар в корзине
        boolean alreadyInCart = false;
        for (int i = 0; i < cartModel.getSize(); i++) {
            ProductCert p = cartModel.getElementAt(i);
            if (p.getProduct().equals(product.getProduct())) {
                // Обновляем количество товара в корзине
                alreadyInCart = true;
                p.getProductInfo().setQuantity(p.getProductInfo().getQuantity() + quantity);
                // Обновляем цену товара на складе
                long newQuantity = p.getProduct().getQuantity() - quantity;
                p.getProduct().setQuantity(newQuantity);
                // Обновляем цену товара на складе (если требуется)
                // p.getProduct().setPrice(newPrice);
                cartModel.setElementAt(p, i);
                break;
            }
        }

        // Если товара нет в корзине, добавляем его
        if (!alreadyInCart) {
            ProductInfo productInfo = new ProductInfo(product.getProductInfo().getQuantity(), product.getProductInfo().getPrice());
            productInfo.setQuantity(quantity);
            ProductCert newProductCert = new ProductCert(product.getProduct(), productInfo);
            cartModel.addElement(newProductCert);
        }

        // Обновляем количество товара на складе (если требуется)
        long newQuantity = product.getProductInfo().getQuantity() - quantity;
        product.getProductInfo().setQuantity(newQuantity);


        Double totalPrice = 0.0;
        for (int i = 0; i < cartModel.getSize(); i++) {
            ProductCert p = cartModel.getElementAt(i);
            totalPrice += p.getProduct().getPrice() * p.getProductInfo().getQuantity();
        }
        total.setText(String.valueOf(totalPrice));
    }
}
