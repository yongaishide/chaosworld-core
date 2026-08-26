package com.yongaishide.chaosworld.item.custom.cell;

import appeng.core.localization.GuiText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.math.BigInteger;

import static appeng.core.localization.Tooltips.*;

/** Tooltips com formatação humanizada (K, M, B, T) para bytes e types */
public class AEUniversalTooltips
{
    // ────────────────── FORMATAÇÃO HUMANIZADA ──────────────────

    /**
     * Formata um número longo com sufixos humanizados.
     * Ex: 1024 → "1.02K", 40000000 → "40M", 750000000 → "750M"
     * Números abaixo de 1000 são exibidos normalmente.
     */
    private static final long K = 1024L;
    private static final long M = K * 1024L;
    private static final long G = M * 1024L;
    private static final long T = G * 1024L;
    private static final long P = T * 1024L;

    static String formatHumanReadable(long value) {
        if (value < 0) return "∞";
        if (value < K) return String.valueOf(value);
        if (value < M) {
            return value / K + "K";
        }
        if (value < G) {
            double v = value / (double) M;
            return (v == (long) v) ? (long) v + "M" : String.format("%.1fM", v);
        }
        if (value < T) {
            double v = value / (double) G;
            return (v == (long) v) ? (long) v + "G" : String.format("%.1fG", v);
        }
        if (value < P) {
            double v = value / (double) T;
            return (v == (long) v) ? (long) v + "T" : String.format("%.1fT", v);
        }
        double v = value / (double) P;
        return (v == (long) v) ? (long) v + "P" : String.format("%.1fP", v);
    }

    /**
     * Formata um BigInteger com sufixos humanizados.
     */
    private static String formatHumanReadable(BigInteger value) {
        if (value.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0 && value.signum() >= 0) {
            return formatHumanReadable(value.longValue());
        }
        // Para valores gigantescos além de long
        BigInteger T = BigInteger.valueOf(1_000_000_000_000L);
        BigInteger[] divRem = value.divideAndRemainder(T);
        return divRem[0].toString() + "T";
    }

    // ────────────────── BYTES USED ──────────────────

    public static Component bytesUsed(long bytes, long max)
    {
        if (max <= 0) {
            MutableComponent inf = Component.literal("∞").withStyle(GREEN);
            return of(GuiText.BytesUsed,
                    of(
                            ofUnformattedNumberWithRatioColor(bytes, 0.0, false),
                            of(" "),
                            of(GuiText.Of),
                            of(" "),
                            inf
                    )
            );
        }

        MutableComponent maxComp = Component.literal(formatHumanReadable(max)).withStyle(ChatFormatting.GRAY);
        return of(GuiText.BytesUsed,
                of(
                        ofUnformattedNumberWithRatioColor(bytes, (double) bytes / (double) max, false),
                        of(" "),
                        of(GuiText.Of),
                        of(" "),
                        maxComp
                )
        );
    }

    public static Component bytesUsed(BigInteger bytes, long max)
    {
        String bytesFormatted = formatHumanReadable(bytes);
        MutableComponent bytesString = Component.literal(bytesFormatted).withStyle(GREEN).withStyle(colorFromRatio(0.0, false));

        if (max <= 0) {
            MutableComponent inf = Component.literal("∞").withStyle(GREEN);
            return of(GuiText.BytesUsed,
                    of(
                            bytesString,
                            of(" "),
                            of(GuiText.Of),
                            of(" "),
                            inf
                    )
            );
        }

        MutableComponent maxComp = Component.literal(formatHumanReadable(max)).withStyle(ChatFormatting.GRAY);
        return of(GuiText.BytesUsed,
                of(
                        bytesString,
                        of(" "),
                        of(GuiText.Of),
                        of(" "),
                        maxComp
                )
        );
    }

    // ────────────────── TYPES USED ──────────────────

    public static Component typesUsed(long types, long max)
    {
        if (max <= 0) {
            MutableComponent inf = Component.literal("∞").withStyle(GREEN);
            return of(
                    ofUnformattedNumberWithRatioColor(types, 0.0, false),
                    of(" "),
                    of(GuiText.Of),
                    of(" "),
                    inf,
                    of(" "),
                    of(GuiText.Types)
            );
        }

        MutableComponent maxComp = Component.literal(formatHumanReadable(max)).withStyle(ChatFormatting.GRAY);
        return of(
                ofUnformattedNumberWithRatioColor(types, (double) types / (double) max, false),
                of(" "),
                of(GuiText.Of),
                of(" "),
                maxComp,
                of(" "),
                of(GuiText.Types)
        );
    }
}