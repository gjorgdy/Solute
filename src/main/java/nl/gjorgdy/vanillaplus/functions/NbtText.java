package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.nbt.NbtString;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class NbtText {

    public static NbtString of(String text) {
        return of(Text.literal(text), Formatting.WHITE);
    }

    public static NbtString of(String text, Formatting color) {
        return of(Text.literal(text), color);
    }

    // Get white text
    public static NbtString of(MutableText text) {
        return of(text, Formatting.WHITE);
    }

    public static NbtString of(MutableText text, Formatting color) {
        return NbtString.of(
                Text.Serializer.toJson(
                    text.setStyle(Style.EMPTY.withItalic(false).withColor(color))
                )
        );
    }

}
