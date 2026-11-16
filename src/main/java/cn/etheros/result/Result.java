package cn.etheros.result;

import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * 通用业务结果类型：同时携带业务数据与结果码
 *
 * <p>结果码必须是实现了 {@link ResultCode} 的枚举，用 {@link #of(Object, Enum)} 或 {@link #of(Enum)} 创建。
 *
 * @param <T> 业务数据的类型
 * @param <E> 结果码的枚举类型
 * @author 西博卡兹
 * @since 1.0.0
 */
@Getter
public class Result<T, E extends Enum<E> & ResultCode> {

    private final @Nullable T data;
    private final @NonNull E resultCode;

    private Result(@Nullable T data, @NonNull E resultCode) {
        this.data = data;
        this.resultCode = resultCode;
    }

    /**
     * 创建携带业务数据的结果
     *
     * @param data 业务数据，允许为 {@code null}
     * @param code 结果码，不能为 {@code null}
     * @param <T>  业务数据的类型
     * @param <E>  结果码的枚举类型
     * @return 结果对象
     */
    public static @NonNull <T, E extends Enum<E> & ResultCode> Result<T, E> of(
            @Nullable T data,
            @NonNull E code
    ) {
        return new Result<>(data, code);
    }

    /**
     * 创建只携带结果码、没有业务数据的结果
     *
     * @param code 结果码，不能为 {@code null}
     * @param <T>  业务数据的类型
     * @param <E>  结果码的枚举类型
     * @return 结果对象，{@link #getData()} 为 {@code null}
     */
    public static @NonNull <T, E extends Enum<E> & ResultCode> Result<T, E> of(
            @NonNull E code
    ) {
        return of(null, code);
    }
}
