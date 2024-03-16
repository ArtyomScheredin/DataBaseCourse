package integration;

import integration.utils.EmployeesServiceMock;
import integration.utils.RefundsDaoImplMock;
import org.junit.jupiter.api.*;
import ru.scheredin.dao.RefundsDao;
import ru.scheredin.dto.Refund;
import ru.scheredin.services.*;

import java.util.List;
import java.util.Objects;

@Tags({
        @Tag("integration"),
        @Tag("anna")
})
public class CreateRefundTest {
    public static final String LOGIN = "user";
    public static final int ORDER_ID  = 0;
    public static final int NEW_ORDER_ID  = 1;
    public static final String DESCRIPTION  = "some info";
    public static final String NEW_DESCRIPTION  = "new info";
    private final RefundsDao refundsDao = new RefundsDaoImplMock();
    private final EmployeesService employeesService = new EmployeesServiceMock();
    private final RefundsService refundsService = new RefundsServiceImpl(refundsDao, employeesService);

    @BeforeEach
    void setUp() {
        refundsService.createRefund(ORDER_ID, DESCRIPTION);
    }

    @DisplayName("Оформление нового возврата из RefundsController\n")
    @Test
    void createRefundTest() {
        //when
        boolean refundAlreadyExists = refundsService.createRefund(ORDER_ID, NEW_DESCRIPTION);
        boolean emptyDescription = refundsService.createRefund(ORDER_ID, "");
        boolean successfulTest = refundsService.createRefund(NEW_ORDER_ID, NEW_DESCRIPTION);
        //then
        Assertions.assertFalse(refundAlreadyExists);
        Assertions.assertFalse(emptyDescription);
        Assertions.assertTrue(successfulTest);
    }

    @DisplayName("Просмотр назначенных на сотрудника возвратов из RefundsController\n")
    @Test
    void getAssignedRefundsTest() {
        //give
        refundsService.createRefund(NEW_ORDER_ID, NEW_DESCRIPTION);
        //when
        List<Refund> refunds = refundsService.getAssignedRefunds(LOGIN);
        //then
        Assertions.assertFalse(refunds.isEmpty());
        Assertions.assertTrue(refunds.stream().anyMatch(refund ->
                refund.getOrder_id() == NEW_ORDER_ID
                && Objects.equals(refund.getDescription(), NEW_DESCRIPTION)));
    }

    @DisplayName("Согласование сотрудником возврата из RefundsController\n")
    @Test
    void approveRefundTest() {
        //give
        refundsService.createRefund(NEW_ORDER_ID, NEW_DESCRIPTION);
        //when
        boolean notExists = refundsService.approveRefund(3);
        boolean successApprove = refundsService.approveRefund(0);
        //then
        Assertions.assertFalse(notExists);
        Assertions.assertFalse(successApprove);
    }

    @DisplayName("Просмотр обновленного списка возвратов клиентом из RefundsController\n")
    @Test
    void getMyRefundsTest() {
        //give
        refundsService.createRefund(NEW_ORDER_ID, NEW_DESCRIPTION);
        //when
        List<Refund> refunds = refundsService.getMyRefunds(LOGIN);
        //then
        Assertions.assertFalse(refunds.isEmpty());
        Assertions.assertTrue(refunds.stream().anyMatch(refund ->
                refund.getOrder_id() == NEW_ORDER_ID
                        && Objects.equals(refund.getDescription(), NEW_DESCRIPTION)));
    }
}
