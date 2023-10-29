package bootiful.moduliths.orders;

public record OrderPlacedEvent(Integer orderId, Integer customerId, Integer productId) {
}
