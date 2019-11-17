package com.jtk.matching.core;

import static com.jtk.matching.api.avro.AvroUtil.convertToBigDecimal;
import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.MsgType;
import static com.jtk.matching.api.gen.enums.MsgType.Amend;
import static com.jtk.matching.api.gen.enums.MsgType.New;
import com.jtk.matching.api.gen.enums.OrderType;
import com.jtk.matching.core.exp.ValidationException;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.math.BigDecimal;

public class OrderBookValidation {

    public static void validateAmendOrder(Order amendOrder) throws ValidationException {
        OrderBookValidationResult result = new OrderBookValidationResult(FALSE,"Amend Order validated");
        if (amendOrder.getMsgType() != Amend) {
            result =  new OrderBookValidationResult(TRUE, "Only amend orders accepted");
        } else if (amendOrder.getQuantity() <= 0) {
            result = new OrderBookValidationResult(TRUE, "Order cannot have quantity less than or equal to zero");
        } else if (convertToBigDecimal(amendOrder.getPrice(), OrderBook.getPriceScale()).compareTo(BigDecimal.ZERO) <= 0 &&
                amendOrder.getOrderType() == OrderType.LIMIT) {
            result = new OrderBookValidationResult(TRUE, "Limit Order cannot have price less than or equal to zero");
        }

        check(result);
    }

    public static void validateNewOrder(Order newOrder) throws ValidationException {
        OrderBookValidationResult result = new OrderBookValidationResult(FALSE,"New Order validated");
        if (newOrder.getMsgType() != New) {
            result = new OrderBookValidationResult(TRUE, "Only New orders accepted");
        } else if (newOrder.getQuantity() <= 0) {
            result = new OrderBookValidationResult(TRUE, "Order cannot have quantity less than or equal to zero");
        } else if (convertToBigDecimal(newOrder.getPrice(), OrderBook.getPriceScale()).compareTo(BigDecimal.ZERO) <= 0 &&
                newOrder.getOrderType() == OrderType.LIMIT) {
            result = new OrderBookValidationResult(TRUE, "Limit Order cannot have price less than or equal to zero");
        }
        check(result);
    }

    public static void validateCancelOrder(Order cancelOrder) throws ValidationException {
        OrderBookValidationResult result = new OrderBookValidationResult(FALSE,"Cancel Order validated");
        if (cancelOrder.getMsgType() != MsgType.Cancel) {
            result = new OrderBookValidationResult(TRUE, "Only Cancel orders accepted");
        }
        check(result);
    }

    private static void check(OrderBookValidationResult result) throws ValidationException {
        if(result.isHasFailed()){
            throw new ValidationException(result.getMessage());
        }
    }



    static class OrderBookValidationResult {
        private boolean hasFailed;
        private String message;

        OrderBookValidationResult(boolean hasFailed, String message) {
            this.hasFailed = hasFailed;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public boolean isHasFailed() {
            return hasFailed;
        }
    }
}
