

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class JSerialPortTool extends JFrame implements SerialPortEventListener {

	private static final long serialVersionUID = 1L;

	private SerialPort serialPort;

	private CommPortIdentifier portId;

	private InputStream in;

	private OutputStream out;

	private static final int BUFFER_SIZE = 16384;

	private static final int RECEIVE_TIMEOUT = 30 * 1000;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;

	private JLabel jLabel = null;

	private JComboBox serialPortJComboBox = null;

	private JLabel jLabel1 = null;

	private JComboBox baudRateJComboBox = null;

	private JButton openJButton = null;

	private JScrollPane logJScrollPane = null;

	private JTextArea logJTextArea = null;

	private JPanel jPanel1 = null;

	private JLabel jLabel2 = null;

	private JTextField atCommandJTextField = null;

	private JButton sendJButton = null;

	private JPanel baseJPanel = null;

	private JCheckBox endWithJCheckBox = null;

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("BAUD:");
			jLabel = new JLabel();
			jLabel.setText("PORT:");
			jPanel = new JPanel();
			jPanel.setLayout(new FlowLayout());
			jPanel.add(jLabel, null);
			jPanel.add(getSerialPortComboBox(), null);
			jPanel.add(jLabel1, null);
			jPanel.add(getBaudRateJComboBox(), null);
			jPanel.add(getOpenJButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes serialPortComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSerialPortComboBox() {
		if (serialPortJComboBox == null) {
			serialPortJComboBox = new JComboBox();
			serialPortJComboBox.setPreferredSize(new Dimension(120, 20));
			serialPortJComboBox.setEditable(true);
		}
		return serialPortJComboBox;
	}

	/**
	 * This method initializes baudRateJComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getBaudRateJComboBox() {
		if (baudRateJComboBox == null) {
			baudRateJComboBox = new JComboBox();
			baudRateJComboBox.setPreferredSize(new Dimension(120, 20));
			baudRateJComboBox.setEditable(true);
			baudRateJComboBox.setModel(new DefaultComboBoxModel(new String[] {
					"9600", "115200", "230400", "460800", "921600" }));
		}
		return baudRateJComboBox;
	}

	/**
	 * This method initializes openJButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOpenJButton() {
		if (openJButton == null) {
			openJButton = new JButton();
			openJButton.setText("OPEN");
			openJButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String port = serialPortJComboBox.getSelectedItem()
							.toString().trim();
					int baud = Integer.parseInt(baudRateJComboBox
							.getSelectedItem().toString().trim());
					try {
						if (e.getActionCommand().equals("OPEN")) {
							if (baud < 0) {
								return;
							}
							openJButton.setText("CLOSE");
							if (openPort(port, baud)) {
								openJButton.setText("CLOSE");
								serialPortJComboBox.setEnabled(false);
								baudRateJComboBox.setEnabled(false);
								atCommandJTextField.setEnabled(true);
								atCommandJTextField.requestFocus();
								sendJButton.setEnabled(true);
							}

						} else if (e.getActionCommand().equals("CLOSE")) {

							if (closePort()) {
								openJButton.setText("OPEN");
								serialPortJComboBox.setEnabled(true);
								baudRateJComboBox.setEnabled(true);
								atCommandJTextField.setEnabled(false);
								atCommandJTextField.setText("");
								logJTextArea.setText("");
								sendJButton.setEnabled(false);
							}
						}
					} catch (Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			});
		}
		return openJButton;
	}

	/**
	 * This method initializes logJScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getLogJScrollPane() {
		if (logJScrollPane == null) {
			logJScrollPane = new JScrollPane();
			logJScrollPane.setViewportView(getLogJTextArea());
		}
		return logJScrollPane;
	}

	/**
	 * This method initializes logJTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getLogJTextArea() {
		if (logJTextArea == null) {
			logJTextArea = new JTextArea();
//			Font font = new Font("ËÎÌå", Font.PLAIN, 12);
//			logJTextArea.setFont(font);
			logJTextArea.setBackground(new java.awt.Color(0, 0, 0));
			logJTextArea.setColumns(20);
			logJTextArea.setEditable(false);
			logJTextArea.setForeground(java.awt.Color.green);
			logJTextArea.setLineWrap(true);
			logJTextArea.setRows(5);
			logJTextArea.setWrapStyleWord(true);
			logJTextArea.setDoubleBuffered(true);
		}
		return logJTextArea;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("AT:");
			jPanel1 = new JPanel();
			jPanel1.setLayout(new FlowLayout());
			jPanel1.add(jLabel2, null);
			jPanel1.add(getAtCommandJTextField(), null);
			jPanel1.add(getEndWithJCheckBox(), null);
			jPanel1.add(getSendJButton(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes atCommandJTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getAtCommandJTextField() {
		if (atCommandJTextField == null) {
			atCommandJTextField = new JTextField();
			atCommandJTextField.setPreferredSize(new Dimension(210, 20));
			atCommandJTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyReleased(java.awt.event.KeyEvent e) {
					int key = e.getKeyChar();
					if (key == 10) {
						// enter¼ü
						sendAT();
					}
					if (key == 26) {
						// ctrl+z¼ü
						try {
							String sms = atCommandJTextField.getText()
									.toString();
							send(sms + (char) 26);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			});
		}
		return atCommandJTextField;
	}

	private void sendAT() {
		if (atCommandJTextField.getText().trim().equalsIgnoreCase("cls")) {
			logJTextArea.setText("");
			atCommandJTextField.setText("");
		} else {
			try {
				if (endWithJCheckBox.isSelected()) {
					send(atCommandJTextField.getText().toString() + "\r");
				} else {
					send(atCommandJTextField.getText().toString());
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * This method initializes sendJButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSendJButton() {
		if (sendJButton == null) {
			sendJButton = new JButton();
			sendJButton.setText("SEND");
			sendJButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					sendAT();
				}
			});
		}
		return sendJButton;
	}

	/**
	 * This method initializes baseJPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBaseJPanel() {
		if (baseJPanel == null) {
			baseJPanel = new JPanel();
			baseJPanel.setLayout(new BorderLayout());
			baseJPanel.add(getJPanel(), BorderLayout.NORTH);
			baseJPanel.add(getJPanel1(), BorderLayout.SOUTH);
		}
		return baseJPanel;
	}

	/**
	 * This method initializes endWithJCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getEndWithJCheckBox() {
		if (endWithJCheckBox == null) {
			endWithJCheckBox = new JCheckBox();
			endWithJCheckBox.setText("'\\r'");
			endWithJCheckBox.setSelected(true);
		}
		return endWithJCheckBox;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JSerialPortTool thisClass = new JSerialPortTool();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public JSerialPortTool() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(450, 500);
		this.setContentPane(getJContentPane());
		this.setTitle("JSerialPortTool");
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int width = this.getWidth();
		int height = this.getHeight();
		this.setBounds((d.width - width) / 2, (d.height - height) / 2, width,
				height);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String serialPort = null;
			ArrayList ls = new ArrayList();
			TreeSet set = new TreeSet();
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier portIdentifier = (CommPortIdentifier) portEnum
						.nextElement();
				if (CommPortIdentifier.PORT_SERIAL == portIdentifier
						.getPortType()) {
					serialPort = portIdentifier.getName();
					set.add(serialPort);
				}
			}
			Iterator it = set.iterator();
			while (it.hasNext()) {
				serialPort = it.next().toString();
				serialPortJComboBox.addItem(serialPort);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (serialPortJComboBox.getItemCount() == 0) {
			serialPortJComboBox.setEnabled(false);
			baudRateJComboBox.setEnabled(false);
			atCommandJTextField.setEnabled(false);
			openJButton.setEnabled(false);
			sendJButton.setEnabled(false);
		} else {
			serialPortJComboBox.setEnabled(true);
			baudRateJComboBox.setEnabled(true);
			atCommandJTextField.setEnabled(false);
			openJButton.setEnabled(true);
			sendJButton.setEnabled(false);
		}
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getBaseJPanel(), BorderLayout.NORTH);
			jContentPane.add(getLogJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private boolean openPort(String comPort, int baudRate) {
		try {
			System.out.println("Open:" + comPort + "@" + baudRate);
			portId = CommPortIdentifier.getPortIdentifier(comPort);
			serialPort = (SerialPort) portId.open("SerialPortTool", 2010);
			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();
			serialPort.notifyOnDataAvailable(true);
			serialPort.notifyOnOutputEmpty(true);
			serialPort.notifyOnBreakInterrupt(true);
			serialPort.notifyOnFramingError(true);
			serialPort.notifyOnOverrunError(true);
			serialPort.notifyOnParityError(true);
			serialPort.setDTR(true);
			serialPort.setRTS(true);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.addEventListener(this);
			serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setInputBufferSize(BUFFER_SIZE);
			serialPort.setOutputBufferSize(BUFFER_SIZE);
			serialPort.enableReceiveTimeout(RECEIVE_TIMEOUT);
			return true;
		} catch (Exception ex) {
			JOptionPane.showConfirmDialog(null, "Open Failed!", "Warning",
					JOptionPane.CLOSED_OPTION);
		}
		return false;
	}

	private boolean closePort() {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (serialPort != null)
				serialPort.close();
			return true;
		} catch (Exception ex) {
			JOptionPane.showConfirmDialog(null, "Close Failed", "Warning",
					JOptionPane.CLOSED_OPTION);
		}
		return false;
	}

	private void send(String s) throws IOException {
		System.out.println("SEND :" + formatLog(new StringBuffer(s)));
		for (int i = 0; i < s.length(); i++) {
			write(s.charAt(i));
		}
	}

	private void write(char c) throws IOException {
		out.write(c);
		out.flush();
	}

	private void clear() throws IOException {
		while (portHasData()) {
			read();
		}
	}

	private boolean portHasData() throws IOException {
		return (in.available() > 0);
	}

	private int read() throws IOException {
		return in.read();
	}

	private String formatLog(StringBuffer s) {
		StringBuffer response = new StringBuffer();
		int i;
		char c;

		for (i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			switch (c) {
			case 13:
				response.append("(cr)");
				break;
			case 10:
				response.append("(lf)");
				break;
			case 9:
				response.append("(tab)");
				break;
			default:
				if (((int) c >= 32) && ((int) c < 128)) {
					response.append(c);
				} else {
					response.append("(" + (int) c + ")");
				}
				break;
			}
		}
		return response.toString();
	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
			break;
		case SerialPortEvent.OE:
			break;
		case SerialPortEvent.FE:
			break;
		case SerialPortEvent.PE:
			break;
		case SerialPortEvent.CD:
			break;
		case SerialPortEvent.CTS:
			break;
		case SerialPortEvent.DSR:
			break;
		case SerialPortEvent.RI:
			break;
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			int c = -1;
			StringBuffer buffer = new StringBuffer(BUFFER_SIZE);
			StringBuffer debugbuffer = new StringBuffer(BUFFER_SIZE);
			try {
				c = read();
				while (c != -1) {
					buffer.append((char) c);
					String debugs = Integer.toHexString(c);
					if (debugs.length() == 1) {
						debugs = "0" + debugs;
					}
					debugbuffer.append(debugs);
					if (!portHasData()) {
						break;
					}
					c = read();
				}
				System.out.println("ECHO :" + this.formatLog(buffer));
				System.out.println("DUMP :" + debugbuffer.toString().toUpperCase());
				String response = buffer.toString();
				logJTextArea.append(response);
				logJTextArea.setCaretPosition(this.logJTextArea.getText()
						.length());
				logJTextArea.repaint();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

	}

}
