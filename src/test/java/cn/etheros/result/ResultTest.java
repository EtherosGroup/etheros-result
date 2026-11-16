package cn.etheros.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Result} 工厂方法与 {@link ResultCode} 扩展约定
 */
class ResultTest {

    /** 测试用业务结果码，验证使用方可自行扩展 */
    private enum TestCode implements ResultCode {
        Success,
        Invalid
    }

    @Test
    void ofCarriesDataAndResultCode() {
        Result<String, TestCode> result = Result.of("hi", TestCode.Success);

        assertEquals("hi", result.getData());
        assertSame(TestCode.Success, result.getResultCode());
    }

    @Test
    void ofWithoutDataLeavesDataNull() {
        Result<Void, TestCode> result = Result.of(TestCode.Invalid);

        assertNull(result.getData());
        assertSame(TestCode.Invalid, result.getResultCode());
    }

    @Test
    void allowsNullData() {
        Result<String, TestCode> result = Result.of(null, TestCode.Invalid);

        assertNull(result.getData());
        assertSame(TestCode.Invalid, result.getResultCode());
    }

    @Test
    void worksWithCommonResultCodes() {
        Result<Void, CommonResultCode> result = Result.of(CommonResultCode.Success);

        assertNull(result.getData());
        assertSame(CommonResultCode.Success, result.getResultCode());
    }

    @Test
    void genericInferenceKeepsConcreteEnumType() {
        // 编译器推断为 Result<Integer, CommonResultCode>，结果码不需要强转
        Result<Integer, CommonResultCode> result = Result.of(42, CommonResultCode.Success);
        CommonResultCode code = result.getResultCode();

        assertEquals(42, result.getData());
        assertSame(CommonResultCode.Success, code);
    }

    @Test
    void voidResultIsUsableAsResultCode() {
        assertTrue(ResultCode.class.isAssignableFrom(VoidResult.class));
        assertEquals(0, VoidResult.values().length);
    }
}
