package view;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CollectionViewStaticElements {
    public static Border paddingBorder(int px) {
        return new EmptyBorder(px, px, px, px);
    }
}
