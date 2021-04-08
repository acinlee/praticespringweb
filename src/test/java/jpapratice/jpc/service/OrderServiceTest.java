package jpapratice.jpc.service;

import jpapratice.jpc.domain.Address;
import jpapratice.jpc.domain.Member;
import jpapratice.jpc.domain.Order;
import jpapratice.jpc.domain.OrderStatus;
import jpapratice.jpc.domain.item.Book;
import jpapratice.jpc.domain.item.Item;
import jpapratice.jpc.exception.NotEnoughStockException;
import jpapratice.jpc.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        Member member = getMember("회원1");

        Item book = getItem("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);

    }

    @Test
    public void 주문취소() throws Exception {
        Member member = getMember("회원1");
        Item item = getItem("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(item.getStockQuantity()).isEqualTo(10);
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        Member member = getMember("회원1");
        Item item = getItem("시골 JPA", 10000, 10);

        orderService.order(member.getId(), item.getId(), 11);

        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    private Member getMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "광진구", "123-556"));
        em.persist(member);
        return member;
    }

    private Item getItem(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}