package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDto;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
1. 엔티티 조회 방식으로 우선 접근
    1. 페치조인으로 쿼리 수를 최적화
    2. 컬렉션 최적화
        1. 페이징 필요 hibernate.default_batch_fetch_size , @BatchSize 로 최적화
        2. 페이징 필요X 페치 조인 사용
2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate
 */


@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // Entity 직접 노출하기 때문에 비추
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy 강제 초기화
        }
        return all;
    }

    // 엔티티 조회 후 DTO로 변환: V2
    @GetMapping("/api/v2/orders")
    public OrderResponse ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return new OrderResponse("testkey321", collect);
    }

    @Data
    @AllArgsConstructor
    static class OrderResponse {
        private String key;
        private List<OrderDto> data;
    }

    // 아래는 OSIV 종료 모드를 함께 적용한 것임.
    // (대용량 트래픽 서버에서는 무조건 OSIV를 끄고 써야하기 때문에,
    // 핵심비지니스로직(OrderService) / 화면이나 API에 맞춘 서비스, 주로 읽기 전용 트랜잭션(OrderQueryService) 로 나눠서
    // 두 서비스 모두 트랜잭션을 유지하면서 '지연로딩!!'을 사용할 수 있다!!
    // spring.jpa.open-in-view: false
    private final OrderQueryService orderQueryService;

    // 페치 조인으로 쿼리 수 최적화: V3
    // N대일 관계만 조회할 때 사용 (중복데이터가 발생하지않고, 페이징이 필요하지 않을때)
    // 쿼리가 딱 1번 나감. (모두 fetch join 으로 불러옴)
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return orderQueryService.ordersV3();
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 최적화
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */
    // 아래도 OSIV off 모드를 함께 적용한것임.
    // N대다 관계가 하나라도 존재하거나 페이징이 필요할때 사용 (중복데이터가 발생하기 때문에.)
    // 쿼리 총 3번 나간다. (Order 조회할때 member, delivery 함께 fetch join 해서 조회, orderItem 조회할때 in 써서, ITEM 조회할때 in 써서)
    // IN(?, ? ..) 안에는 최대 1000개 hibernate.default_batch_fetch_size 로 조정.
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset",defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue= "100") int limit) {
        return orderQueryService.ordersV3_page(offset, limit);
    }

    // JPA에서 DTO를 직접 조회: V4
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        List<OrderQueryDto> orders = orderQueryRepository.findOrderQueryDtos();
        return orders;
    }

    // 컬렉션 조회 최적화 - 일대다 관계인 컬렉션은 IN 절을 활용해서 메모리에 미리 조회해서 최적화: V5
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        List<OrderQueryDto> orders = orderQueryRepository.findAllByDto_optimization();
        return orders;
    }

    // 플랫 데이터 최적화 - JOIN 결과를 그대로 조회 후 애플리케이션에서 원하는 모양으로 직접 변환: V6
    // 쿼리가 한방만 나가는데, 그렇게 추천하는것은 아니다.
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6() {
        List<OrderFlatDto> orders = orderQueryRepository.findAllByDto_flat();
        return orders;
    }
}
