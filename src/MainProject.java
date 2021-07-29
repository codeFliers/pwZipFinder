import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javax.swing.JOptionPane.showMessageDialog;

public class MainProject  extends JFrame implements ActionListener {

    private JButton impZipButton;private JButton impWLButton;private JButton researchButton;
    private JTextPane textPane;
    private JList jlistLeftZip;private JList jlistRightWL;
    private JScrollPane infoScrollPane;private JScrollPane zipScrollPane;private JScrollPane wlScrollPane;
    private JLabel jLabelImpZip;private JLabel jLabelImpWL;
    private DefaultListModel zipListModel;private DefaultListModel wlListModel;
    private JPanel contentPane;private JPanel importButtonPanel;
    private JPanel listPanel;private JPanel researchPanel;
    private ArrayList listValideFormat;
    private Map<File, String> resultMap;
    private File[] listOfWL;private File[] listOfZip;
    Map<File, File[]> filesListOfWL;
    boolean leftTrue = false;boolean rightTrue = false;

    public MainProject() {
        this.resultMap = new HashMap<>();
        this.filesListOfWL = new HashMap<>();
        this.listValideFormat = new ArrayList();
        this.addFormat("zip", "txt");//valide file format
        zipListModel = this.setModelFromFilePathFormats("import/", "*.zip");
        wlListModel = this.setModelFromFilePathFormats("wordlist/", "*.txt");
        impZipButton = new JButton("IMPORT ZIP");impWLButton = new JButton("IMPORT WL");researchButton = new JButton("FIND THE PASSWORD");
        jlistLeftZip = new JList();jlistRightWL = new JList();
        jlistLeftZip.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jlistRightWL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        infoScrollPane = new JScrollPane();zipScrollPane = new JScrollPane();wlScrollPane = new JScrollPane();
        jLabelImpZip = new JLabel("Zip list: ");jLabelImpWL = new JLabel("wordlist list: ");
        textPane = new JTextPane();textPane.setEditable(false);textPane.setContentType("text/html");
        contentPane = new JPanel();importButtonPanel = new JPanel();listPanel = new JPanel();researchPanel = new JPanel();

        this.createAndShowGUI();
    }

    protected void createAndShowGUI() {
        impZipButton.setPreferredSize(new Dimension(150, 50));
        impWLButton.setPreferredSize(new Dimension(150, 50));
        researchButton.setPreferredSize(new Dimension(200, 50));
        importButtonPanel.add(impZipButton);importButtonPanel.add(impWLButton);

        impZipButton.addActionListener(this);impZipButton.setActionCommand("importZipButton");
        impWLButton.addActionListener(this);impWLButton.setActionCommand("importWLButton");
        researchButton.addActionListener(this);researchButton.setActionCommand("researchButton");

        jLabelImpZip.setLabelFor(jlistLeftZip);jLabelImpWL.setLabelFor(jlistRightWL);
        jlistLeftZip.setModel(zipListModel);jlistRightWL.setModel(wlListModel);
        zipScrollPane.setViewportView(jlistLeftZip);wlScrollPane.setViewportView(jlistRightWL);
        listPanel.add(zipScrollPane);listPanel.add(wlScrollPane);

        researchPanel.add(researchButton);researchPanel.add(infoScrollPane);
        researchPanel.setPreferredSize(new Dimension(600, 600));

        zipScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        zipScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        zipScrollPane.setPreferredSize(new Dimension(200, 300));

        wlScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        wlScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        wlScrollPane.setPreferredSize(new Dimension(200, 300));

        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        //

        infoScrollPane.setViewportView(textPane);
        infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        infoScrollPane.setPreferredSize(new Dimension(500, 500));
        infoScrollPane.setViewportView(textPane);//replace the add

        contentPane.add(importButtonPanel, BorderLayout.NORTH);
        contentPane.add(listPanel, BorderLayout.CENTER);
        contentPane.add(researchPanel, BorderLayout.SOUTH);
        add(contentPane);
        //https://stackoverflow.com/questions/34778965/how-to-remove-auto-focus-in-swing
        getContentPane().requestFocusInWindow(); //leave the default focus to the JFrame


        researchButton.setEnabled(false);
        setVisible(true);//making the frame visible
        setResizable(false);//not resizable, fixed
        setSize(600, 1000);
        setLocationRelativeTo(null);//center
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        this.jlistLeftZip.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getValueIsAdjusting())
                    return;

                //enable the corresponding button
                if (list.getSelectedValue() == null) {
                    leftTrue = false;
                    researchButton.setEnabled(false);
                } else {
                    leftTrue = true;
                    if(rightTrue == true) {
                        researchButton.setEnabled(true);
                    }
                }
            }
        });
        this.jlistRightWL.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getValueIsAdjusting())
                    return;

                //enable the corresponding button
                if (list.getSelectedValue() == null) {
                    rightTrue = false;
                    researchButton.setEnabled(false);

                } else {
                    rightTrue = true;
                    if(leftTrue == true) {
                        researchButton.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionName = e.getActionCommand();
        if(actionName.equals("importZipButton")) {
            doImport("import/", "zip");
            zipListModel = this.setModelFromFilePathFormats("import/", "*.zip");
            jlistLeftZip.setModel(zipListModel);
        }
        if(actionName.equals("importWLButton")) {
            doImport("wordlist/", "txt");
            wlListModel = this.setModelFromFilePathFormats("wordlist/", "*.txt");
            jlistRightWL.setModel(wlListModel);
        }
        if(actionName.equals("researchButton")) {
            String[] zipSelected;
            zipSelected = new String[jlistLeftZip.getSelectedValuesList().size()];
            zipSelected = (String[]) (jlistLeftZip.getSelectedValuesList()).toArray(zipSelected);

            String[] wlSelected;
            wlSelected = new String[jlistRightWL.getSelectedValuesList().size()];
            wlSelected = (String[]) (jlistRightWL.getSelectedValuesList()).toArray(wlSelected);

            reboot();
            buildFilesListOfWL(zipSelected, wlSelected);

            try {
                doResearch();
            } catch (IOException | InterruptedException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    private void reboot() {
        textPane.setText("");
        filesListOfWL.clear();
        resultMap.clear();
    }

    private void doResearch() throws IOException, InterruptedException {
        String listTried = "";
        String userInformation = "";

        for (Map.Entry<File, File[]> entry : filesListOfWL.entrySet()) {
            File key = entry.getKey();
            File[] value = entry.getValue();
            int valueInc = 0;
            int k = 0;
            int x = 0;
            int compareValueInc = -1;
            boolean found = false;
            ZipFile zipFile = new ZipFile(key);

            userInformation += "File <strong>"+key.getName()+"</strong> is being analysed<br/>";
            textPane.setText(userInformation);
            textPane.update(textPane.getGraphics());
            TimeUnit.SECONDS.sleep(1);
            if(zipFile.isEncrypted()) {
                userInformation += "The latter is encrypted with a password<br/>";
                //try out the different lists available
                userInformation += "Researching among the selected list: <br/>";
                textPane.setText(userInformation);
                textPane.update(textPane.getGraphics());
                TimeUnit.SECONDS.sleep(1);
                while(valueInc<value.length) {
                    listTried += value[valueInc].getName() +" ";
                    //try out every password of one of the list available
                    for (String str : readFile(value[valueInc])) {
                        if(compareValueInc != valueInc ) {
                            userInformation += "-Looking in the wordlist <strong>" + value[valueInc].getName() + "</strong>...<br/>";
                            userInformation += "...<br/>";
                            textPane.setText(userInformation);
                            textPane.update(textPane.getGraphics());
                            compareValueInc = valueInc;
                        }
                        System.out.println("STR: "+str);
                        zipFile.setPassword(str.toCharArray());
                        try {
                            String fileNameWithOutExt = FilenameUtils.removeExtension(key.getName());
                            zipFile.extractAll("destination_directory/" + fileNameWithOutExt);
                        } catch (ZipException exception) {
                            x++;
                        }

                        if (x == k) {
                            userInformation += "<font size='4' color='green'>**THE PASSWORD HAS BEEN FOUND !</font> <br/><br/>";
                            textPane.setText(userInformation);
                            textPane.update(textPane.getGraphics());
                            TimeUnit.SECONDS.sleep(1);
                            // userInformation += "**Le mot de passe a été trouvé ! \n\n";
                            String result = "File : <strong>"+key.getName()+"</strong>; Password : <strong>" + str + "</strong> ; First wordlist that found the password : <strong>" + value[valueInc].getName()
                                    +"</strong> ; Different wordlist tested: <strong>"+listTried+"</strong>";
                            resultMap.put(key, result);
                            found = true;
                            break;
                        }

                        k++;
                    }

                    //if i found it in this list i break
                    if(found) {
                        //reset
                        listTried = "";
                        break;
                    }
                    valueInc++;
                }
                //if i didn't found in all the available list
                if(!found) {
                    userInformation += "<font size='4' color='red'>**NO PASSWORD FOUND IN THESE LISTS.</font><br/><br/>";
                    textPane.setText(userInformation);
                    textPane.update(textPane.getGraphics());
                    TimeUnit.SECONDS.sleep(1);
                    String result = "File : <strong>" + key.getName() + "</strong> ; Password : ; Wordlist tried : <strong>" + listTried+"</strong>";
                    resultMap.put(key, result);
                }

            }else {
                userInformation += "This file is not encrypted with a password<br/> End of the research<br/><br/>";
                textPane.setText(userInformation);
                textPane.update(textPane.getGraphics());
                TimeUnit.SECONDS.sleep(1);
            }
        }

        String strResult = "";
        for (Map.Entry<File, String> result : resultMap.entrySet()) {
            //File key = result.getKey();
            strResult += result.getValue()+"<br>";
        }
        textPane.setText(userInformation+=strResult);
        textPane.update(textPane.getGraphics());
        TimeUnit.SECONDS.sleep(1);


    }
    public static java.util.List<String> readFile(File file) throws IOException {
        // Open the file
        FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        List<String> myList = new ArrayList<>();
        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            myList.add(strLine);
        }

        //Close the input stream
        fstream.close();

        return myList;
    }
    private void buildFilesListOfWL(String[] zipSelected, String[] wlSelected) {
        ArrayList<File> arr = new ArrayList<>();
        for(File f : listOfZip) {
            for(String s : zipSelected) {
                if(f.getName().equals(s)) {
                    for(File f2 : listOfWL) {
                        for(String s2 : wlSelected) {
                            if(f2.getName().equals(s2)) {
                                arr.add(f2);
                            }
                        }
                    }
                    File[] fSorted = arr.toArray(new File[arr.size()]);
                    filesListOfWL.put(f, fSorted);
                    arr.clear();
                }
            }
        }
    }
    //CREATE A DEFAULTLISTMODEL OBJECT FROM A LIST OF FILE IN AN ARRAY ( to construct/reconstruct a JList )
    //PRESENT AT A SPECIFIC PATH UNDER SPECIFIC FORMATS
    protected DefaultListModel setModelFromFilePathFormats(String path, String... formats) {
        File[] f = this.getListOfFiles(path, formats);

        if(path.equals("import/")) {
            this.listOfZip = getListOfFiles("import/",  "*.zip");
        }
        if(path.equals("wordlist/")) {
            this.listOfWL = getListOfFiles("wordlist/",  "*.txt");
        }

        DefaultListModel model = new DefaultListModel();
        model.ensureCapacity(200);

        for (File file : f) {
            model.addElement(file.getName());
        }
        return model;
    }
    public static File[] getListOfFiles(String path, String... formats) {
        int nbOfFormat = formats.length;
        String[] typeFile = new String[nbOfFormat];
        int incr = 0;
        for (String format : formats) {
            typeFile[incr] = format;
            incr++;
        }
        File dir = new File(path);
        return dir.listFiles((FileFilter) new WildcardFileFilter(typeFile));
    }
    //Check a format accordingly to the array
    protected boolean checkFormat(String formatInput) {
        if (formatInput.contains(".")) {
            formatInput = formatInput.substring(formatInput.lastIndexOf(".") + 1);
            return listValideFormat.contains(formatInput);
        }
        return false;
    }
    protected void addFormat(String... formats) {
        Collections.addAll(this.listValideFormat, formats);
    }
    protected void doImport(String folderPath, String format) {
        JFileChooser fileChooseBox = new JFileChooser();
        File src = null;
        File dest = new File(folderPath);//"import/", "wordlist/"

        //Filter JFileChooser suggestion (vcf only and by default)
        FileNameExtensionFilter filter = new FileNameExtensionFilter(format+" file", format);
        fileChooseBox.addChoosableFileFilter(filter);//set a filter
        fileChooseBox.setFileFilter(filter);//set as default filter

        //Picked a File
        if (fileChooseBox.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            //selected file
            src = fileChooseBox.getSelectedFile();

            //Check if the file have 'cvf' extension
            boolean cvfExtTrue = this.checkFormat(src.toString());

            if (cvfExtTrue) {
                //Copy a src (file) to a Directory (dest)
                try {
                    FileUtils.copyFileToDirectory(src, dest);
                    showMessageDialog(null, "Your file have been added");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                showMessageDialog(null, "Choose a valide format file ("+format+")");
            }
        } else {
            //
        }
    }
}
