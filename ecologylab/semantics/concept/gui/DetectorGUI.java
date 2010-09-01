package ecologylab.semantics.concept.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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
import ecologylab.semantics.concept.detect.Detector.DetectionListener;

public class DetectorGUI extends JPanel
{

	static class ConceptRecord implements Comparable<ConceptRecord>
	{
		String	surface;

		String	concept;

		double	confidence;

		@Override
		public int compareTo(ConceptRecord other)
		{
			return Double.compare(other.confidence, confidence);
		}

		@Override
		public String toString()
		{
			return String.format("(%.2f) %s -> %s", confidence, surface, concept);
		}
	}

	private Detector							detector;

	private Vector<ConceptRecord>	detected;
	
	private Vector<ConceptRecord>	displayed;

	private JTextArea							textArea;

	private JList									list;

	private JSlider								slider;

	private double								threshold;

	public DetectorGUI()
	{
		super();

		detected = new Vector<ConceptRecord>();

		detector = new Detector();
		detector.addDetectionListener(new DetectionListener()
		{
			@Override
			public void conceptDetected(String surface, String concept, boolean prediction,
					double confidence)
			{
				ConceptRecord rec = new ConceptRecord();
				rec.surface = surface;
				rec.concept = concept;
				rec.confidence = confidence;
				detected.add(rec);

				filterAndUpdate();
			}
		});

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);

		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(400, 300));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

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
							detector.detect(textArea.getText());
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

		final int n = 100;
		slider = new JSlider(-n, n, 0);
		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider slider = (JSlider) e.getSource();
				if (!slider.getValueIsAdjusting())
				{
					threshold = slider.getValue() / (double) n;
					filterAndUpdate();
				}
			}
		});

		list = new JList();

		add(textArea);
		add(btn);
		add(slider);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);

		setBorder(border);
	}

	public void filterAndUpdate()
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

	public static void createGUI()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DetectorGUI gui = new DetectorGUI();
		frame.setContentPane(gui);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		createGUI();
	}

}
