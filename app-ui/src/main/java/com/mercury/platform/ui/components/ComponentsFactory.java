package com.mercury.platform.ui.components;

import com.mercury.platform.core.misc.SoundType;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.fields.style.MercuryComboBoxUI;
import com.mercury.platform.ui.components.fields.style.MercuryScrollBarUI;
import com.mercury.platform.ui.components.panel.misc.ToggleCallback;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;

/**
 * Factory for each element which uses in application
 */
public class ComponentsFactory {
    private final static Logger log = LogManager.getLogger(ComponentsFactory.class);

    private Font BOLD_FONT;
    private Font ITALIC_FONT;
    private Font REGULAR_FONT;
    private Font SMALLCAPS_FONT;
    @Getter
    private Font DEFAULT_FONT;
    private float scale = 1f;

    public ComponentsFactory() {
        loadFonts();
        this.scale = 1.0f;

//        UIManager.getDefaults().put("Slider.horizontalThumbIcon",this.getImage("app/slider_thumb.png"));
        UIManager.put("ComboBox.selectionBackground", AppThemeColor.HEADER);
        UIManager.put("ComboBox.selectionForeground", AppThemeColor.ADR_POPUP_BG);
        UIManager.put("ComboBox.disabledForeground", AppThemeColor.ADR_FOOTER_BG);
    }

    /**
     * Loading all application fonts
     */
    private void loadFonts() {
        try {
        	String defaultFontName = new JLabel().getFont().getName();
            BOLD_FONT = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/Fontin-Bold.ttf"));
            ITALIC_FONT = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/Fontin-Italic.ttf"));
            REGULAR_FONT = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/Fontin-Regular.ttf"));
            SMALLCAPS_FONT = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/Fontin-SmallCaps.ttf"));
            DEFAULT_FONT = new Font(defaultFontName, Font.BOLD, (int) (scale * 16));
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Get button with custom params
     *
     * @param fontStyle  path of exile font type
     * @param background button background
     * @param border     button border
     * @param text       default text
     * @param fontSize   font size
     * @return JButton object
     */
    public JButton getButton(FontStyle fontStyle, Color background, Border border, String text, float fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintBorder(Graphics g) {
                if (!this.getModel().isPressed()) {
                    super.paintBorder(g);
                }
            }
        };
        button.setBackground(background);
        button.setForeground(AppThemeColor.TEXT_DEFAULT);
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            Border prevBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                this.prevBorder = button.getBorder();
                CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppThemeColor.ADR_SELECTED_BORDER, 1),
                        BorderFactory.createLineBorder(button.getBackground(), 3)
                );
                button.setBorder(compoundBorder);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(prevBorder);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        button.addActionListener(action -> {
            MercuryStoreCore.soundSubject.onNext(SoundType.CLICKS);
        });
        if (isAscii(text)) {
            button.setFont(getSelectedFont(fontStyle).deriveFont(scale * fontSize));
        } else {
            button.setFont(DEFAULT_FONT.deriveFont(scale * fontSize));
        }
        button.setBorder(border);
        button.addChangeListener(e -> {
            if (!button.getModel().isPressed()) {
                button.setBackground(button.getBackground());
            }
        });
        return button;
    }

    /**
     * Get button with default properties
     *
     * @param text text on button
     * @return Default app button
     */
    public JButton getButton(String text) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );

        return getButton(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * 14f);
    }

    /**
     * Get bordered button with default properties.
     *
     * @param text text on button
     * @return Default bordered app button
     */
    public JButton getBorderedButton(String text) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );
        return getButton(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * 14f);
    }

    public JButton getBorderedButton(String text, float fontSize) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );
        return getButton(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * fontSize);
    }

    public JButton getBorderedButton(String text, float fontSize, Color background, Color outerBorderColor, Color innerBorderColor) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(outerBorderColor, 1),
                BorderFactory.createLineBorder(innerBorderColor, 3)
        );
        return getButton(FontStyle.BOLD, background, compoundBorder, text, scale * fontSize);
    }

    public Component setUpToggleCallbacks(Component button, ToggleCallback firstState, ToggleCallback secondState, boolean initialState) {
        button.addMouseListener(new MouseAdapter() {
            private boolean state = initialState;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (state) {
                    firstState.onToggle();
                    state = false;
                } else {
                    secondState.onToggle();
                    state = true;
                }
            }
        });
        return button;
    }

    /**
     * Get button with icon
     *
     * @param iconPath icon path from maven resources
     * @param iconSize icon size
     * @return JButton object with icon
     */
    public JButton getIconButton(String iconPath, float iconSize, Color background, String tooltip) {
        JButton button = new JButton("") {
            @Override
            protected void paintBorder(Graphics g) {
                if (!this.getModel().isPressed()) {
                    super.paintBorder(g);
                }
            }
        };
        button.setBackground(background);
        button.setFocusPainted(false);
        button.addChangeListener(e -> {
            if (!button.getModel().isPressed()) {
                button.setBackground(button.getBackground());
            }
        });
        if (tooltip.length() > 0) {
            button.addMouseListener(new TooltipMouseListener(tooltip));
        }
        button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        button.addActionListener(action -> {
            MercuryStoreCore.soundSubject.onNext(SoundType.CLICKS);
            button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        });
        button.addMouseListener(new MouseAdapter() {
            Border prevBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                prevBorder = button.getBorder();
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppThemeColor.ADR_SELECTED_BORDER),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(prevBorder);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

        });
        button.setBorder(BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 4));
        button.setVerticalAlignment(SwingConstants.CENTER);
        BufferedImage icon = null;
        try {
            BufferedImage buttonIcon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
            icon = Scalr.resize(buttonIcon, (int) (scale * iconSize));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (icon != null) {
            button.setIcon(new ImageIcon(icon));
        }
        return button;
    }

    public JButton getIconifiedTransparentButton(String iconPath, String tooltip) {
        JButton iconButton = getIconButton(iconPath, 10, AppThemeColor.FRAME_RGB, tooltip);
        iconButton.setIcon(getImage(iconPath));
        return iconButton;
    }

    /**
     * Get bordered default button with icon
     *
     * @param iconPath icon path from maven resources
     * @param iconSize icon size
     * @return bordered JButton with icon
     */
    public JButton getBorderedIconButton(String iconPath, int iconSize, String tooltip) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 2)
        );
        JButton iconButton = getIconButton(iconPath, iconSize, AppThemeColor.FRAME_ALPHA, tooltip);
        iconButton.setBorder(BorderFactory.createLineBorder(AppThemeColor.BUTTON, 2));
        return iconButton;
    }

    /**
     * Get icon button with custom size
     *
     * @param iconPath   icon path from maven resources
     * @param iconSize   icon size
     * @param buttonSize button size (its only preferred)
     * @return JButton with icon
     */
    public JButton getIconButton(String iconPath, int iconSize, Dimension buttonSize, String tooltip) {
        JButton iconButton = getIconButton(iconPath, iconSize, AppThemeColor.FRAME_ALPHA, tooltip);
        iconButton.setPreferredSize(buttonSize); //todo scale
        iconButton.setSize(buttonSize);
        return iconButton;
    }

    /**
     * Get label with custom params
     *
     * @param fontStyle path of exile font type
     * @param frColor   foreground color
     * @param alignment font alignment
     * @param size      font size
     * @param text      initial text on font
     * @return JLabel object
     */
    public JLabel getTextLabel(FontStyle fontStyle, Color frColor, TextAlignment alignment, float size, String text) {
        JLabel label = new JLabel(text);
        if (isAscii(text)) {
            label.setFont(getSelectedFont(fontStyle).deriveFont(scale * size));
        } else {
            label.setFont(DEFAULT_FONT.deriveFont(scale * size));
        }
        label.setForeground(frColor);
        Border border = label.getBorder();
        label.setBorder(new CompoundBorder(border, new EmptyBorder(0, 5, 0, 5)));

        if (alignment != null) {
            switch (alignment) {
                case LEFTOP: {
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                break;
                case RIGHTOP: {
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                case CENTER: {
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                break;
            }
        }
        return label;
    }

    public JLabel getTextLabel(FontStyle fontStyle, Color frColor, TextAlignment alignment, float size, Border border, String text) {
        JLabel textLabel = getTextLabel(fontStyle, frColor, alignment, size, text);
        textLabel.setBorder(border);
        return textLabel;
    }

    /**
     * Get default label
     *
     * @param text font text
     * @return JLabel object
     */
    public JLabel getTextLabel(String text) {
        return getTextLabel(FontStyle.BOLD, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, scale * 15f, text);
    }

    public JLabel getTextLabel(String text, FontStyle style) {
        return getTextLabel(style, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, scale * 15f, text);
    }

    public JLabel getTextLabel(String text, FontStyle style, Color color) {
        return getTextLabel(style, color, TextAlignment.LEFTOP, scale * 15f, text);
    }

    public JLabel getTextLabel(String text, FontStyle style, float size) {
        return getTextLabel(style, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, size, text);
    }

    public JLabel getTextLabel(String text, FontStyle style, Color color, float size) {
        return getTextLabel(style, color, TextAlignment.LEFTOP, size, text);
    }

    /**
     * Get label with icon
     *
     * @param iconPath icon path from maven resources
     * @param size     icon size
     * @return JLabel object with icon
     */
    public JLabel getIconLabel(String iconPath, int size) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(iconPath, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        return iconLabel;
    }

    public JLabel getIconLabel(String iconPath, int size, int aligment) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(iconPath, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        iconLabel.setHorizontalAlignment(aligment);
        return iconLabel;
    }

    public JLabel getIconLabel(URL url, int size) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(url, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(url.getPath(), "/", "."));
        }
        return iconLabel;
    }

    public JLabel getIconLabel(String iconPath, int size, int alignment, String tooltip) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(iconPath, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        iconLabel.setHorizontalAlignment(alignment);
        iconLabel.addMouseListener(new TooltipMouseListener(tooltip));
        return iconLabel;
    }

    public JLabel getIconLabel(String iconPath) {
        JLabel iconLabel = new JLabel();
        try {
            BufferedImage buttonIcon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
            iconLabel.setIcon(new ImageIcon(buttonIcon));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        return iconLabel;
    }

    public JTextField getTextField(String text) {
        JTextField textField = getTextField(text, null, scale * 16);
        textField.setFont(DEFAULT_FONT);
        return textField;
    }

    public JFormattedTextField getIntegerTextField(Integer min, Integer max, Integer value) {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);

        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setValue(value);
        field.setFont(REGULAR_FONT.deriveFont(scale * 18));
        field.setFocusLostBehavior(JFormattedTextField.PERSIST);
        field.setForeground(AppThemeColor.TEXT_DEFAULT);
        field.setCaretColor(AppThemeColor.TEXT_DEFAULT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        field.setBackground(AppThemeColor.HEADER);
        return field;
    }

    public JTextField getTextField(String text, FontStyle style, float fontSize) {
        JTextField textField = new JTextField(text);
        if (style != null) {
            textField.setFont(getSelectedFont(style).deriveFont(scale * fontSize));
        }
        textField.setForeground(AppThemeColor.TEXT_DEFAULT);
        textField.setCaretColor(AppThemeColor.TEXT_DEFAULT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        textField.setBackground(AppThemeColor.HEADER);
        return textField;
    }

    public JCheckBox getCheckBox(String tooltip) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setFocusPainted(false);
        checkBox.setBackground(AppThemeColor.TRANSPARENT);
//        checkBox.setUI(new WindowsButtonUI());
        checkBox.addMouseListener(new TooltipMouseListener(tooltip));
        return checkBox;
    }

    public JCheckBox getCheckBox(boolean value) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(value);
//        checkBox.setUI(new WindowsButtonUI());
        checkBox.setFocusPainted(false);
        checkBox.setBackground(AppThemeColor.TRANSPARENT);
        return checkBox;
    }

    public JCheckBox getCheckBox(boolean value, String tooltip) {
        JCheckBox checkBox = this.getCheckBox(tooltip);
        checkBox.setSelected(value);
        return checkBox;
    }

    public Font getFontByLang(String text, FontStyle style) {
        if (style != null) {
            if (isAscii(text)) {
                return getSelectedFont(style);
            }
        }
        return DEFAULT_FONT;
    }

    public JPanel getSliderSettingsPanel(JLabel titleLabel, JLabel countLabel, JSlider slider) {
        Dimension elementsSize = convertSize(new Dimension(160, 30));
        Dimension countSize = convertSize(new Dimension(40, 30));
        titleLabel.setPreferredSize(elementsSize);
        slider.setPreferredSize(elementsSize);
        countLabel.setPreferredSize(countSize);
        JPanel panel = getTransparentPanel(new GridBagLayout());
        panel.setBackground(AppThemeColor.ADR_BG);
        GridBagConstraints titleGc = new GridBagConstraints();
        GridBagConstraints countGc = new GridBagConstraints();
        GridBagConstraints sliderGc = new GridBagConstraints();
        titleGc.weightx = 0.5f;
        countGc.weightx = 0.1f;
        sliderGc.weightx = 0.4f;
        titleGc.fill = GridBagConstraints.HORIZONTAL;
        countGc.fill = GridBagConstraints.HORIZONTAL;
        sliderGc.fill = GridBagConstraints.HORIZONTAL;
        titleGc.anchor = GridBagConstraints.NORTHWEST;
        countGc.anchor = GridBagConstraints.NORTHWEST;
        sliderGc.anchor = GridBagConstraints.NORTHWEST;
        titleGc.gridx = 0;
        countGc.gridx = 1;
        sliderGc.gridx = 2;

        panel.add(titleLabel, titleGc);
        panel.add(countLabel, countGc);
        panel.add(slider, sliderGc);
        return panel;
    }

    public JPanel getSettingsPanel(JLabel titleLabel, Component component) {
        JPanel panel = getTransparentPanel(new GridLayout(1, 2));
        panel.setBackground(AppThemeColor.ADR_BG);
        panel.add(titleLabel);
        panel.add(component);
        return panel;
    }

    public Font getFont(FontStyle style, float fontSize) {
        return getSelectedFont(style).deriveFont(scale * fontSize);
    }

    public JComboBox getComboBox(String[] child) {
        JComboBox comboBox = new JComboBox<>(child);
        comboBox.setBackground(AppThemeColor.HEADER);
        comboBox.setForeground(AppThemeColor.TEXT_DEFAULT);
        comboBox.setFont(BOLD_FONT.deriveFont(scale * 16f));
        comboBox.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER, 1));
        comboBox.setUI(MercuryComboBoxUI.createUI(comboBox));
        return comboBox;
    }

    public JSlider getSlider(int min, int max, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
//        slider.setPaintLabels(true);
//        slider.setUI(new WindowsSliderUI(slider));
        slider.setForeground(AppThemeColor.TEXT_DEFAULT);
        slider.setFont(REGULAR_FONT.deriveFont(15f));
        slider.setRequestFocusEnabled(false);
        slider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                slider.getParent().repaint();
            }
        });
        slider.setBackground(AppThemeColor.FRAME);
        return slider;
    }

    public JSlider getSlider(int min, int max, int value, Color background) {
        JSlider slider = this.getSlider(min, max, value);
        slider.setBackground(background);
        return slider;
    }

    public JScrollPane getVerticalContainer(JPanel container) {
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.setBackground(AppThemeColor.FRAME);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.addMouseWheelListener(e -> MercuryStoreUI.scrollToEndSubject.onNext(false));

        container.getParent().setBackground(AppThemeColor.TRANSPARENT);
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setBackground(AppThemeColor.SLIDE_BG);
        vBar.setUI(new MercuryScrollBarUI());
        vBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE));
        vBar.setUnitIncrement(3);
        vBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        return scrollPane;
    }

    public JPanel getTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(AppThemeColor.TRANSPARENT);
        return panel;
    }

    public JPanel getTransparentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppThemeColor.TRANSPARENT);
        return panel;
    }

    public JPanel getBorderedTransparentPanel(Border border, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(AppThemeColor.TRANSPARENT);
        panel.setBorder(border);
        return panel;
    }

    public ImageIcon getIcon(String iconPath, float size) {
        BufferedImage icon = null;
        try {
            BufferedImage buttonIcon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
            icon = Scalr.resize(buttonIcon, (int) (scale * size));
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public ImageIcon getIcon(URL iconPath, float size) {
        BufferedImage icon = null;
        try {
            BufferedImage buttonIcon = ImageIO.read(iconPath);
            icon = Scalr.resize(buttonIcon, (int) (scale * size));
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public ImageIcon getImage(String iconPath) {
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public Dimension convertSize(Dimension initialSize) {
        return new Dimension((int) (initialSize.width * scale), (int) (initialSize.height * scale));
    }

    public JTextArea getSimpleTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setBackground(AppThemeColor.TRANSPARENT);
        area.setBorder(null);
        area.setFont(REGULAR_FONT.deriveFont(scale * 16f));
        area.setForeground(AppThemeColor.TEXT_DEFAULT);
        return area;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    private Font getSelectedFont(FontStyle fontStyle) {
        switch (fontStyle) {
            case BOLD:
                return BOLD_FONT;
            case ITALIC:
                return ITALIC_FONT;
            case REGULAR:
                return REGULAR_FONT;
            case SMALLCAPS:
                return SMALLCAPS_FONT;
        }
        return DEFAULT_FONT;
    }

    private boolean isAscii(CharSequence sequence) {
        if (sequence != null) {
            for (int i = sequence.length() - 1; i >= 0; i--) {
                if (!matches(sequence.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matches(char c) {
        return c <= '\u007f';
    }

    public JPanel getJPanel(LayoutManager layoutManager) {
        JPanel panel = new JPanel(layoutManager);
        panel.setBackground(AppThemeColor.FRAME);
        return panel;
    }

    public JPanel getJPanel(LayoutManager layoutManager, Color bg) {
        JPanel panel = new JPanel(layoutManager);
        panel.setBackground(bg);
        return panel;
    }

    public JPanel wrapToSlide(JComponent panel) {
        JPanel wrapper = this.getJPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    public JPanel wrapToSlide(JComponent panel, Color bg) {
        JPanel wrapper = this.wrapToSlide(panel);
        wrapper.setBackground(bg);
        return wrapper;
    }

    public JPanel wrapToSlide(JComponent panel, Color bg, int top, int left, int bottom, int righ) {
        JPanel wrapper = this.wrapToSlide(panel, top, left, bottom, righ);
        wrapper.setBackground(bg);
        return wrapper;
    }

    public JPanel wrapToSlide(JComponent panel, int top, int left, int bottom, int right) {
        JPanel wrapper = this.wrapToSlide(panel);
        wrapper.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return wrapper;
    }

    public JPanel wrapToAdrSlide(JComponent panel, int top, int left, int bottom, int right) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            public void remove(Component comp) {
                panel.remove(comp);
            }

            @Override
            public Component add(Component comp) {
                return panel.add(comp);
            }
        };
        wrapper.setBackground(AppThemeColor.FRAME);
        wrapper.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    public JPopupMenu getContextPanel() {
        JPopupMenu contextMenu = new JPopupMenu();
        contextMenu.setBackground(AppThemeColor.FRAME);
        contextMenu.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER));
        contextMenu.setFont(REGULAR_FONT.deriveFont(scale * 16f));
        contextMenu.setForeground(AppThemeColor.TEXT_DEFAULT);
        return contextMenu;
    }

    public JMenuItem getMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(REGULAR_FONT.deriveFont(scale * 16f));
        menuItem.setForeground(AppThemeColor.TEXT_DEFAULT);
        return menuItem;
    }

    public JMenuItem getMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(REGULAR_FONT.deriveFont(scale * 16f));
        menu.setForeground(AppThemeColor.TEXT_DEFAULT);
        return menu;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private class TooltipMouseListener extends MouseAdapter {
        private String tooltip;

        @Override
        public void mouseEntered(MouseEvent e) {
            MercuryStoreCore.tooltipSubject.onNext(tooltip);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            MercuryStoreCore.tooltipSubject.onNext(null);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            MercuryStoreCore.tooltipSubject.onNext(null);
        }
    }
}
