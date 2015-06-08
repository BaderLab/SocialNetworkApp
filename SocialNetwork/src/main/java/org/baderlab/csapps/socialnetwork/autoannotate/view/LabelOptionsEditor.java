package org.baderlab.csapps.socialnetwork.autoannotate.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.baderlab.csapps.socialnetwork.autoannotate.model.AnnotationSet;
import org.baderlab.csapps.socialnetwork.autoannotate.model.LabelOptions;

public class LabelOptionsEditor extends JDialog {

    private static final long serialVersionUID = -883735369366365835L;

    private static final Map<Double, String> labelOffsetXToString;
    static {
        HashMap<Double, String> aMap = new HashMap<Double, String>();
        aMap.put(0.0, "Left");
        aMap.put(0.5, "Center");
        aMap.put(1.0, "Right");
        labelOffsetXToString = Collections.unmodifiableMap(aMap);
    }

    private static final Map<Double, String> labelOffsetYToString;
    static {
        HashMap<Double, String> aMap = new HashMap<Double, String>();
        aMap.put(0.0, "Above");
        aMap.put(0.5, "Center");
        aMap.put(1.0, "Below");
        labelOffsetYToString = Collections.unmodifiableMap(aMap);
    }

    private int maxWords;
    private List<Integer> wordSizeThresholds;
    private int sameClusterBonus;
    private int centralityBonus;
    private boolean applied;

    private JPanel slidersPanel;

    protected ArrayList<JSlider> thresholdSliders;

    private JPanel innerPanel;

    private JComboBox<Integer> maximumLabelLengthDropdown;

    private JSlider sameClusterBonusSlider;
    private JSlider centralityBonusSlider;

    private double labelPositionX;
    private double labelPositionY;

    private JComboBox<String> verticalPositionDropdown;

    private JComboBox<String> justificationDropdown;

    public LabelOptionsEditor(AnnotationSet selectedAnnotationSet) {
        super();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.applied = false;
        if (selectedAnnotationSet != null) {
            this.maxWords = selectedAnnotationSet.getMaxWords();
            this.wordSizeThresholds = selectedAnnotationSet.getWordSizeThresholds();
            this.thresholdSliders = new ArrayList<JSlider>();
            double[] labelPosition = selectedAnnotationSet.getLabelPosition();
            this.labelPositionX = labelPosition[0];
            this.labelPositionY = labelPosition[1];
            this.sameClusterBonus = selectedAnnotationSet.getSameClusterBonus();
            this.centralityBonus = selectedAnnotationSet.getCentralityBonus();
            setTitle("Text Label Options");

            this.innerPanel = new JPanel();
            this.innerPanel.setLayout(new BoxLayout(this.innerPanel, BoxLayout.PAGE_AXIS));
            this.innerPanel.add(createThresholdSliderPanel());
            this.innerPanel.add(createBonusSliderPanel());
            this.innerPanel.add(createLabelPositionDropdownsPanel());
            this.innerPanel.add(createButtonPanel());
            add(this.innerPanel);
            pack();
        } else {
            JOptionPane.showMessageDialog(null, "Please create an annotation set", "Error Message",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    public JPanel createBonusSliderPanel() {
        JPanel bonusSliderPanel = new JPanel();
        bonusSliderPanel.setLayout(new BoxLayout(bonusSliderPanel, BoxLayout.PAGE_AXIS));
        bonusSliderPanel.setBorder(BorderFactory.createTitledBorder("Word Size Bonuses"));
        // Bonus for words in the same WordCloud cluster
        final JLabel sameClusterBonusLabel = new JLabel("Font size bonus for words in same WordCloud cluster: " +
                this.sameClusterBonus);
        this.sameClusterBonusSlider = new JSlider(0, 15, this.sameClusterBonus);
        this.sameClusterBonusSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider thisSlider = (JSlider) e.getSource();
                LabelOptionsEditor.this.sameClusterBonus = thisSlider.getValue();
                // Update label to show value
                String labelText = sameClusterBonusLabel.getText();
                String newThresholdText = labelText.substring(0, labelText.indexOf(":"))
                        + ": " + thisSlider.getValue();
                sameClusterBonusLabel.setText(newThresholdText);
                revalidate();
            }
        });
        bonusSliderPanel.add(sameClusterBonusLabel);
        bonusSliderPanel.add(this.sameClusterBonusSlider);
        // Bonus for words coming from the most central nodes in the cluster
        final JLabel centralityBonusLabel = new JLabel("Font size bonus for words from most central nodes: " +
                this.centralityBonus);
        this.centralityBonusSlider = new JSlider(0, 15, this.centralityBonus);
        this.centralityBonusSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider thisSlider = (JSlider) e.getSource();
                LabelOptionsEditor.this.centralityBonus = thisSlider.getValue();
                // Update label to show value
                String labelText = centralityBonusLabel.getText();
                String newThresholdText = labelText.substring(0, labelText.indexOf(":"))
                        + ": " + thisSlider.getValue();
                centralityBonusLabel.setText(newThresholdText);
                revalidate();
            }
        });

        this.sameClusterBonusSlider.setToolTipText("Sliders to adjust font size bonus given to words in the same cluster as ones already in the label");
        this.centralityBonusSlider.setToolTipText("Sliders to adjust font size bonus given to words from the most central node");

        bonusSliderPanel.add(centralityBonusLabel);
        bonusSliderPanel.add(this.centralityBonusSlider);

        return bonusSliderPanel;
    }

    public JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        // Closes the dialog window
        JButton defaultButton = new JButton("Restore Defaults");
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double[] labelPosition = AnnotationSet.DEFAULT_LABEL_POSITION;
                LabelOptionsEditor.this.labelPositionX = labelPosition[0];
                LabelOptionsEditor.this.labelPositionY = labelPosition[1];
                LabelOptionsEditor.this.justificationDropdown.setSelectedItem(labelOffsetXToString.get(LabelOptionsEditor.this.labelPositionX));
                LabelOptionsEditor.this.verticalPositionDropdown.setSelectedItem(labelOffsetYToString.get(LabelOptionsEditor.this.labelPositionY));
                // Update the view
                LabelOptionsEditor.this.maximumLabelLengthDropdown.setSelectedItem(AnnotationSet.DEFAULT_MAX_WORDS);
                LabelOptionsEditor.this.wordSizeThresholds = AnnotationSet.DEFAULT_WORDSIZE_THRESHOLDS;
                for (int sliderIndex = 0; sliderIndex < LabelOptionsEditor.this.thresholdSliders.size(); sliderIndex++) {
                    LabelOptionsEditor.this.thresholdSliders.get(sliderIndex).setValue(LabelOptionsEditor.this.wordSizeThresholds.get(sliderIndex));
                }
                LabelOptionsEditor.this.sameClusterBonusSlider.setValue(AnnotationSet.DEFAULT_SAME_CLUSTER_BONUS);
                LabelOptionsEditor.this.centralityBonusSlider.setValue(AnnotationSet.DEFAULT_CENTRALITY_BONUS);
            }
        });
        // Closes the dialog window
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Closes the dialog window and passes back the data
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LabelOptionsEditor.this.applied = true;
                dispose();
            }
        });
        buttonPanel.add(defaultButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        return buttonPanel;
    }

    private JPanel createLabelPositionDropdownsPanel() {
        JPanel labelPositionDropdownsPanel = new JPanel();
        labelPositionDropdownsPanel.setBorder(BorderFactory.createTitledBorder("Label position options"));

        String[] justifications = {"Left", "Center", "Right"};
        DefaultComboBoxModel<String> justificationModel = new DefaultComboBoxModel<String>(justifications);
        this.justificationDropdown = new JComboBox<String>(justificationModel);
        this.justificationDropdown.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                @SuppressWarnings("unchecked")
                String selectedJustification = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
                if (selectedJustification.equals("Left")) {
                    LabelOptionsEditor.this.labelPositionX = 0.0;
                } else if (selectedJustification.equals("Center")) {
                    LabelOptionsEditor.this.labelPositionX = 0.5;
                } else {
                    LabelOptionsEditor.this.labelPositionX = 1.0;
                }
            }
        });
        this.justificationDropdown.setSelectedItem(labelOffsetXToString.get(this.labelPositionX));
        labelPositionDropdownsPanel.add(new JLabel("Horizontal Position: "));
        labelPositionDropdownsPanel.add(this.justificationDropdown);

        String[] verticalPositions = {"Above", "Center", "Below"};
        DefaultComboBoxModel<String> verticalPositionModel = new DefaultComboBoxModel<String>(verticalPositions);
        this.verticalPositionDropdown = new JComboBox<String>(verticalPositionModel);
        this.verticalPositionDropdown.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                @SuppressWarnings("unchecked")
                String selectedPosition = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
                if (selectedPosition.equals("Above")) {
                    LabelOptionsEditor.this.labelPositionY = 0.0;
                } else if (selectedPosition.equals("Center")) {
                    LabelOptionsEditor.this.labelPositionY = 0.5;
                } else {
                    LabelOptionsEditor.this.labelPositionY = 1.0;
                }
            }
        });
        this.verticalPositionDropdown.setSelectedItem(labelOffsetYToString.get(this.labelPositionY));
        labelPositionDropdownsPanel.add(new JLabel("Vertical Position: "));
        labelPositionDropdownsPanel.add(this.verticalPositionDropdown);

        return labelPositionDropdownsPanel;
    }

    public JPanel createThresholdSliderPanel() {
        final JPanel thresholdPanel = new JPanel(new BorderLayout());
        thresholdPanel.setBorder(BorderFactory.createTitledBorder("Label word threshold options"));

        JLabel dropdownLabel = new JLabel("Maximum label length (words)");

        Integer[] labelLengths = {1, 2, 3, 4, 5, 6, 7};
        DefaultComboBoxModel<Integer> labelLengthModel = new DefaultComboBoxModel<Integer>(labelLengths);
        this.maximumLabelLengthDropdown = new JComboBox<Integer>(labelLengthModel);

        JPanel dropdownPanel = new JPanel();
        dropdownPanel.add(dropdownLabel);
        dropdownPanel.add(this.maximumLabelLengthDropdown);
        thresholdPanel.add(dropdownPanel, BorderLayout.NORTH);

        this.slidersPanel = new JPanel();
        this.slidersPanel.setLayout(new BoxLayout(this.slidersPanel, BoxLayout.PAGE_AXIS));
        thresholdPanel.add(this.slidersPanel);

        this.maximumLabelLengthDropdown.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                thresholdPanel.remove(LabelOptionsEditor.this.slidersPanel);
                LabelOptionsEditor.this.thresholdSliders = new ArrayList<JSlider>();
                LabelOptionsEditor.this.slidersPanel = new JPanel();
                LabelOptionsEditor.this.slidersPanel.setLayout(new BoxLayout(LabelOptionsEditor.this.slidersPanel, BoxLayout.PAGE_AXIS));
                LabelOptionsEditor.this.maxWords = (Integer) LabelOptionsEditor.this.maximumLabelLengthDropdown.getSelectedItem();
                for (int sliderNumber = 1; sliderNumber <  LabelOptionsEditor.this.maxWords; sliderNumber++) {
                    if (sliderNumber == 1) {
                        JLabel slidersLabel = new JLabel("Word Inclusion Threshold(s)");
                        LabelOptionsEditor.this.slidersPanel.add(slidersLabel);
                    }
                    // New sliders are given the default value
                    if (LabelOptionsEditor.this.wordSizeThresholds.size() < sliderNumber) {
                        LabelOptionsEditor.this.wordSizeThresholds.add(AnnotationSet.DEFAULT_WORDSIZE_THRESHOLDS.get(sliderNumber - 1));
                    }
                    int sliderValue = LabelOptionsEditor.this.wordSizeThresholds.get(sliderNumber - 1);
                    final JLabel thresholdLabel = new JLabel("Word " + sliderNumber +
                            " to Word " + String.valueOf(sliderNumber+1) + ": " +
                            String.valueOf(sliderValue) + "%");
                    JSlider thresholdSlider = new JSlider(0, 100, sliderValue);
                    thresholdSlider.setLabelTable(thresholdSlider.createStandardLabels(10));
                    thresholdSlider.setPaintLabels(true);
                    thresholdSlider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            // Update word size thresholds
                            JSlider thisSlider = (JSlider) e.getSource();
                            LabelOptionsEditor.this.wordSizeThresholds = new ArrayList<Integer>();
                            for (JSlider slider : LabelOptionsEditor.this.thresholdSliders) {
                                LabelOptionsEditor.this.wordSizeThresholds.add(slider.getValue());
                            }
                            // Update slider labels to show current value
                            String thresholdText = thresholdLabel.getText();
                            String newThresholdText = thresholdText.substring(0, thresholdText.indexOf(":"))
                                    + ": " + thisSlider.getValue() + "%";
                            thresholdLabel.setText(newThresholdText);
                            revalidate();
                        }
                    });
                    LabelOptionsEditor.this.thresholdSliders.add(thresholdSlider);
                    JPanel sliderPanel = new JPanel();
                    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
                    sliderPanel.add(thresholdLabel);
                    sliderPanel.add(thresholdSlider);
                    LabelOptionsEditor.this.slidersPanel.add(sliderPanel);
                }
                LabelOptionsEditor.this.slidersPanel.setToolTipText("Sliders to adjust thresholds for word inclusion in the label");
                thresholdPanel.add(LabelOptionsEditor.this.slidersPanel, BorderLayout.CENTER);
                thresholdPanel.updateUI();
                pack();
            }
        });
        this.maximumLabelLengthDropdown.setSelectedItem(this.maxWords);

        return thresholdPanel;
    }

    public LabelOptions showDialog() {
        setLocationRelativeTo(null);
        this.setVisible(true);
        if (this.applied) {
            double[] labelPosition = {this.labelPositionX, this.labelPositionY};
            LabelOptions labelOptions = new LabelOptions(this.maxWords, this.wordSizeThresholds,
                    labelPosition, this.sameClusterBonus, this.centralityBonus);
            return labelOptions;
        } else {
            return null;
        }
    }

}
