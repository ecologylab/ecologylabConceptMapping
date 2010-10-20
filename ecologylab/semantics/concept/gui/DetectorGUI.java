package ecologylab.semantics.concept.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ecologylab.semantics.concept.detect.Detector;
import ecologylab.semantics.concept.detect.Doc;
import ecologylab.semantics.concept.detect.TrieDict;
import ecologylab.semantics.concept.detect.Detector.DetectionListener;
import ecologylab.semantics.concept.detect.Instance;

@SuppressWarnings("serial")
public class DetectorGUI extends JPanel
{

	static class ConceptRecord implements Comparable<ConceptRecord>
	{
		String	surface;

		String	concept;

		double	confidence;

		double	disambiguationConfidence;

		@Override
		public int compareTo(ConceptRecord other)
		{
			return Double.compare(other.confidence, confidence);
		}

		@Override
		public String toString()
		{
			return String.format("(%.2f) %s -> %s (%.2f)",
					confidence,
					surface,
					concept,
					disambiguationConfidence
					);
		}
	}

	private TrieDict							dictionary;

	private Detector							detector;

	private Vector<ConceptRecord>	detected;

	private Vector<ConceptRecord>	displayed;

	private JTextArea							textArea;

	private JList									list;

	private JLabel								label;

	private JSlider								slider;

	private double								threshold;

	public DetectorGUI() throws IOException
	{
		super();

		dictionary = TrieDict.load(new File("data/freq-surfaces.dict"));
		detected = new Vector<ConceptRecord>();

		detector = new Detector();
		detector.addDetectionListener(new DetectionListener()
		{
			@Override
			public void conceptDetected(String surface, String concept, boolean prediction,
					double confidence, Instance inst)
			{
				ConceptRecord rec = new ConceptRecord();
				rec.surface = surface;
				rec.concept = concept;
				rec.confidence = confidence;
				rec.disambiguationConfidence = inst.disambiguationConfidence;
				detected.add(rec);

				filterAndUpdate();
			}
		});

		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new GridBagLayout());

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPaneForText = new JScrollPane(textArea);
		scrollPaneForText.setPreferredSize(new Dimension(400, 300));
		scrollPaneForText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JButton btn = new JButton("Detect");
		btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				detected.clear();
				filterAndUpdate();

				Thread t = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							String text = textArea.getText();
							detector.detect(new Doc(text, dictionary));
						}
						catch (IOException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				t.start();
			}
		});

		label = new JLabel();
		label.setText("Threshold: 0.00");

		final int n = 100;
		slider = new JSlider(0, n, 0);
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider slider = (JSlider) e.getSource();
				threshold = slider.getValue() / (double) n;
				label.setText(String.format("Threshold: %.2f", threshold));
				if (!slider.getValueIsAdjusting())
				{
					filterAndUpdate();
				}
			}
		});

		list = new JList();
		JScrollPane scrollPaneForList = new JScrollPane(list);
		scrollPaneForList.setPreferredSize(new Dimension(400, 300));
		scrollPaneForList
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints c = new GridBagConstraints(0, 0, 3, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 4, 4);
		add(scrollPaneForText, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.2;
		add(btn, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.2;
		c.anchor = GridBagConstraints.LINE_END;
		add(label, c);

		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.6;
		c.anchor = GridBagConstraints.CENTER;
		add(slider, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		c.weightx = 1;
		add(scrollPaneForList, c);

		setBorder(border);
	}

	public synchronized void filterAndUpdate()
	{
		Collections.sort(detected);
		displayed = new Vector<ConceptRecord>();
		for (int i = 0; i < detected.size(); ++i)
		{
			ConceptRecord rec = detected.get(i);
			if (rec.confidence < threshold)
				break;
			displayed.add(rec);
		}
		list.setListData(displayed);
		list.validate();
	}

	public static void createGUI() throws IOException
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DetectorGUI gui = new DetectorGUI();
		frame.setContentPane(gui);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws IOException
	{
		createGUI();
	}

}
