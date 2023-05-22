package code.enums;

import com.google.common.base.Functions;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enum stub example that follows low latency good practices.
 * <br>This uses performant look up methods compared to the out of the box "valueOf()" and "values()".
 * <br>Additionally we return nulls as opposed to throwing exceptions when inputs don't correspond to enums.
 *
 * <p>Warnings:
 * <br>1)One must not modify the returned values as to achieve performance we reuse the same collections.
 * <br>2) If we add more values in the enum, we must also update the methods corresponding to them.
 *
 * <p>Using Fix's Urgency/Aggressiveness as an example -
 * <a href="https://www.onixs.biz/fix-dictionary/4.4/tagnum_61.html">Source Link</a>
 */
@SuppressWarnings("unused") // This is a demo class, so we know it's not used.
public enum Urgency {

    NORMAL(0),
    FLASH(1),
    BACKGROUND(2);

    private final int urgencyCode;
    private static final Urgency[] VALUES = values();
    private static final Map<String, Urgency> NAMES = Arrays.stream(VALUES).collect(Collectors.toMap(Urgency::name, Functions.identity()));

    Urgency(final int urgencyCode) {
        this.urgencyCode = urgencyCode;
    }

    /**
     * Method that fetches all values from the enum.
     * <br>This method does NOT call "clone()" of the underlying array which produces less garbage and is faster.
     * <br>Additionally, we use arrays and can skip paying the time price of autoboxing into and out of Integers, had we used LISTs.
     *
     * <p>However, one must NOT mutate the returned array.
     *
     * @return Array of all enum values
     */
    public static Urgency[] getValues() {
        return VALUES;
    }

    /**
     * Get enum value from name.
     *
     * @param name String value of the enum's name.
     * @return Enum mapped to the name provided.
     */
    public static Urgency getByName(String name) {
        return NAMES.get(name);
    }

    /**
     * Get by code with O(1) retrieve complexity. Returns Null if code doesn't exist.
     * <br>We are taking advantage that all numbers are continuous which when compiled will map to a jump map (O(1)) instead of a binary search.
     *
     * @param code urgency code as per FIX's definitions
     * @return Urgency enum object mapped to provided code. Null otherwise.
     */
    public static Urgency getByCode(int code) {
        return switch (code) {
            case 0 -> NORMAL;
            case 1 -> FLASH;
            case 2 -> BACKGROUND;
            default -> null;
        };
    }

    public int getCode() {
        return urgencyCode;
    }
}
