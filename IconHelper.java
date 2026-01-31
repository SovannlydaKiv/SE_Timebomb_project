import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconHelper {
    
    public static ImageIcon createClockIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int padding = size / 12;
        g2.setColor(new Color(59, 130, 246));
        g2.fillOval(padding, padding, size - padding*2, size - padding*2);
        
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(size/16));
        int center = size/2;
        g2.drawLine(center, center, center, center - size/4);
        g2.drawLine(center, center, center + size/6, center);
        
        g2.dispose();
        return new ImageIcon(img);
    }
    
    public static ImageIcon createCalendarIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(59, 130, 246));
        g2.fillOval(2, 2, 20, 20);
        
        g2.setColor(Color.WHITE);
        g2.fillRect(7, 9, 10, 8);
        g2.setColor(new Color(59, 130, 246));
        g2.drawLine(7, 11, 17, 11);
        g2.drawLine(12, 11, 12, 17);
        
        g2.dispose();
        return new ImageIcon(img);
    }
    
    public static ImageIcon createChartIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(168, 85, 247));
        g2.fillOval(2, 2, 20, 20);
        
        g2.setColor(Color.WHITE);
        g2.fillRect(7, 14, 2, 4);
        g2.fillRect(11, 11, 2, 7);
        g2.fillRect(15, 9, 2, 9);
        
        g2.dispose();
        return new ImageIcon(img);
    }
    
    public static ImageIcon createTagIcon() {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(34, 197, 94));
        g2.fillOval(2, 2, 20, 20);
        
        g2.setColor(Color.WHITE);
        int[] xPoints = {8, 12, 16, 12};
        int[] yPoints = {12, 8, 12, 16};
        g2.fillPolygon(xPoints, yPoints, 4);
        
        g2.setColor(new Color(34, 197, 94));
        g2.fillOval(11, 10, 2, 2);
        
        g2.dispose();
        return new ImageIcon(img);
    }
}