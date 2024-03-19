package controllers.Payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.time.LocalDate;

public class PaymentProcess {
    @FXML
    private Button payButton;
    private boolean paymentSuccessful;
    private long reservationAmount; // Price of the reservation

    // Initialize Stripe API key
    static {
        Stripe.apiKey = "sk_test_51OoqOSG5OAXh1DqPRMv2iy486SsrhiOhFRApQe5Map0kL6wFGHd1p2nWI1QGp4dL7wVpVX2VLdZviZFaBjOa4sqG0017NnYUAl";
    }

    // Method to check if payment was successful
    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    public void setPaymentSuccessful(boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }

    public void setReservationAmount(long reservationAmount) {
        this.reservationAmount = reservationAmount;
    }

    @FXML
    private void processPayment(ActionEvent event) {
        try {
            // Create a PaymentIntent with payment details
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(reservationAmount) // Amount in cents (e.g., $10.00)
                    .setCurrency("usd")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);


            // If payment is successful, set paymentSuccessful flag to true
            paymentSuccessful = true;
            // If payment is successful, display success message
            System.out.println("Payment successful. PaymentIntent ID: " + intent.getId());
        } catch (StripeException e) {
            // If payment fails, set paymentSuccessful flag to false
            paymentSuccessful = false;
            // If payment fails, display error message
            System.out.println("Payment failed. Error: " + e.getMessage());
        }
    }
}

