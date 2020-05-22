package blcmm.plugins.skill_organizer.bin;

import blcmm.data.lib.DataManager;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author LightChaosman
 */
class Skill {

    String name;
    String icon;
    transient ImageIcon myIcon = null;
    String objectName;

    ImageIcon getIcon() {
        if (myIcon != null) {
            return myIcon;
        }
        URL strm = getClass().getClassLoader().getResource("blcmm/plugins/skill_organizer/icons/" + (DataManager.isBL2() ? "BL2" : "TPS") + "/" + icon + ".png");
        if (strm == null) {
            return null;
        }
        ImageIcon base = new ImageIcon(strm);
        BufferedImage bi = new BufferedImage(
                base.getIconWidth(),
                base.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        base.paintIcon(null, g, 0, 0);
        g.dispose();
        int[] toClip = new int[4];
        for (int i = 0; i < 4; i++) {
            int xbase = i == 1 ? base.getIconWidth() - 1 : 0;
            int ybase = i == 3 ? base.getIconHeight() - 1 : 0;
            int xmult = i >= 2 ? 1 : 0;
            int ymult = i < 2 ? 1 : 0;
            int range = i < 2 ? base.getIconHeight() : base.getIconWidth();
            int resxmult = i == 0 ? 1 : (i == 1 ? -1 : 0);
            int resymult = i == 2 ? 1 : (i == 3 ? -1 : 0);
            int res = 0;
            outer:
            while (true) {
                for (int z = 0; z < range; z++) {
                    int x = xbase + res * resxmult + z * xmult;
                    int y = ybase + res * resymult + z * ymult;
                    int a = new Color(bi.getRGB(x, y), true).getAlpha();
                    if (a > 0) {
                        break outer;
                    }
                }
                res++;
            }
            toClip[i] = Math.max(0, res - 1);
        }
        BufferedImage bi2 = new BufferedImage(
                base.getIconWidth() - toClip[0] - toClip[1],
                base.getIconHeight() - toClip[2] - toClip[3],
                BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < bi2.getWidth(); x++) {
            for (int y = 0; y < bi2.getHeight(); y++) {
                bi2.setRGB(x, y, bi.getRGB(x + toClip[0], y + toClip[2]));
            }
        }
        myIcon = new ImageIcon(bi2);
        return myIcon;
    }

}
