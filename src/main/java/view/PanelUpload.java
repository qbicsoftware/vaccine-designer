package view;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.*;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.themes.ValoTheme;

import helper.UploaderInput;
import helper.Utils;
import model.DatasetBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * The class {@link PanelUpload} represents a component which allows the user to define column names
 * and to upload epitope prediction data.
 * 
 * @author spaethju
 * 
 */
@SuppressWarnings("serial")
public class PanelUpload extends CustomComponent {

  private UploaderInput inputReceiver;
  private Upload inputUpload;
  private Panel uploadPanel;
  private VerticalLayout panelContent, databaseLayout,hlaExpressionLayout, columnLayout, dataLayout, fileTypeSelectionLayout, uploadLayout, alleleFileSelection;
  private HorizontalLayout buttonLayout, alleleTFLayout;
  private TextField immColTf, distanceColTf, uncertaintyColTf, taaColTf,methodColTf, hlaA1TF, hlaB1TF, hlaC1TF, hlaA2TF, hlaB2TF, hlaC2TF, hlaAEVTF, hlaBEVTF, hlaCEVTF;
  private ComboBox projectSelectionCB;
  private ComboBox alleleFileSelectionCB;
  private Button uploadButton, dataSelectionUploadButton, dataSelectionDatabaseButton, manualButton, databaseButton, nextButton;
  private Grid datasetGrid;
  private BeanItem<DatasetBean> selected;
  private Boolean useDatabase;
  private Boolean hlaAsColumns;
  private Boolean alleleFileUpload;
  private HashMap<String, String> alleles, allele_expressions;

  /**
   * Constructor
   */
  public PanelUpload() {
    init();
  }

  public void init() {
    useDatabase = null;
    hlaAsColumns = null;
    alleles = new HashMap<>();
    allele_expressions = new HashMap<>();
    // Create the upload component and handle all its events
    inputReceiver = new UploaderInput();
    //receiver.getProgress().setVisible(false);
    inputUpload = new Upload("Please Upload your Data", inputReceiver);
    inputUpload.setSizeFull();
    inputUpload.addProgressListener(inputReceiver);
    inputUpload.addFailedListener(inputReceiver);

    // Create the Upload Layout
    panelContent = new VerticalLayout();
    panelContent.setSpacing(true);
    panelContent.setMargin(true);

    uploadLayout = createUpload();
    uploadLayout.setVisible(false);
    buttonLayout = createButtons();
    buttonLayout.setVisible(false);
    columnLayout = createColumnTextFields();
    columnLayout.setVisible(false);
    hlaExpressionLayout = createHlaExpressionTextFields();
    hlaExpressionLayout.setVisible(false);
    databaseLayout = createDatabaseSelection();
    databaseLayout.setVisible(false);
    dataLayout = createDataSelection();
    dataLayout.setVisible(true);
    alleleFileSelection = createAlleleFileSelection();
    alleleFileSelection.setVisible(false);
    fileTypeSelectionLayout = createFileTypeSelection();
    fileTypeSelectionLayout.setVisible(false);

    panelContent.addComponents(dataLayout, alleleFileSelection, fileTypeSelectionLayout, uploadLayout, columnLayout, hlaExpressionLayout, databaseLayout, buttonLayout);
    panelContent.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);


    // Create the panel
    uploadPanel = new Panel();
    uploadPanel.setContent(panelContent);
    setCompositionRoot(uploadPanel);
  }

  public VerticalLayout createUpload() {
    uploadButton = new Button("Upload");
    uploadButton.setEnabled(false);

    VerticalLayout allUploadLayout = new VerticalLayout();
    Label description = createDescriptionLabel("Choose a file from your file system and press upload");

    HorizontalLayout uploadLayout = new HorizontalLayout();
    uploadLayout.setMargin(true);
    uploadLayout.setSpacing(true);
    uploadLayout.addComponents(inputUpload, uploadButton);

    allUploadLayout.addComponents(description, uploadLayout);

    return allUploadLayout;
  }

  public HorizontalLayout createButtons() {
    HorizontalLayout buttonsLayout = new HorizontalLayout();
    buttonsLayout.setSizeFull();
    buttonsLayout.setSpacing(true);
    buttonsLayout.setMargin(new MarginInfo(false, false, true, false));
    Button backButton = new Button("Back");
    backButton.setIcon(FontAwesome.ARROW_CIRCLE_O_LEFT);
    backButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    backButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    backButton.addStyleName(ValoTheme.BUTTON_HUGE);
    nextButton = new Button("Next");
    nextButton.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
    nextButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
    nextButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    nextButton.addStyleName(ValoTheme.BUTTON_HUGE);

    buttonsLayout.addComponents(backButton, nextButton);
    buttonsLayout.setComponentAlignment(backButton, Alignment.MIDDLE_CENTER);
    buttonsLayout.setComponentAlignment(nextButton, Alignment.MIDDLE_CENTER);

    backButton.addClickListener((Button.ClickListener) event -> {
      if (dataLayout.isVisible()){
      } else if (alleleFileSelection.isVisible()) {
        alleleFileSelection.setVisible(false);
        dataLayout.setVisible(true);
        buttonLayout.setVisible(false);
      } else if (fileTypeSelectionLayout.isVisible()) {
        fileTypeSelectionLayout.setVisible(false);
        nextButton.setVisible(false);
        if (!useDatabase) {
          dataLayout.setVisible(true);
          buttonLayout.setVisible(false);
        } else {
          alleleFileSelection.setVisible(true);
        }
      } else if (columnLayout.isVisible()) {
        columnLayout.setVisible(false);
        fileTypeSelectionLayout.setVisible(true);
      } else if (hlaExpressionLayout.isVisible()) {
        hlaExpressionLayout.setVisible(false);
        columnLayout.setVisible(true);
      } else if (uploadLayout.isVisible() || databaseLayout.isVisible()) {
        uploadLayout.setVisible(false);
        databaseLayout.setVisible(false);
        hlaExpressionLayout.setVisible(true);
        nextButton.setVisible(true);
      }
    });

    nextButton.addClickListener((Button.ClickListener) event -> {
      if (dataLayout.isVisible()) {
      } else if (alleleFileSelection.isVisible()){

      } else if (fileTypeSelectionLayout.isVisible()) {
        if (hlaAsColumns != null) {
          fileTypeSelectionLayout.setVisible(false);
          columnLayout.setVisible(true);
        } else {}
      } else if (columnLayout.isVisible()) {
        if (immColTf.isValid()) {
          columnLayout.setVisible(false);
          hlaExpressionLayout.setVisible(true);
          if (alleleFileUpload) {
            alleleTFLayout.setVisible(false);
          } else {
            alleleTFLayout.setVisible(true);
          }
        } else {
          Utils.notification("Error", "Please enter a column name for the immunogenicity/score", "error");
        }
      } else if (hlaExpressionLayout.isVisible()) {
        if (isHlaExpressionlValid()) {
          if (!alleleFileUpload) {
            if (isHlalValid()) {
              alleles.put("A1", hlaA1TF.getValue());
              alleles.put("A2", hlaA2TF.getValue());
              alleles.put("B1", hlaB1TF.getValue());
              alleles.put("B2", hlaB2TF.getValue());
              alleles.put("C1", hlaA1TF.getValue());
              alleles.put("C2", hlaA1TF.getValue());
            } else {
              Utils.notification("Error", "Please enter valid HLA-alleles", "error");
            }
          }
          allele_expressions.put("A", hlaAEVTF.getValue());
          allele_expressions.put("B", hlaBEVTF.getValue());
          allele_expressions.put("C", hlaCEVTF.getValue());
          hlaExpressionLayout.setVisible(false);
          nextButton.setVisible(false);
        if (useDatabase) {
          databaseLayout.setVisible(true);
        } else if (!useDatabase) {
          uploadLayout.setVisible(true);
        }
      } } else {
        Utils.notification("Error", "Please enter valid HLA-expression values", "error");
      }
    });
    return buttonsLayout;
  }

  /**
   * Creates the layout including text fields to define the column names of the data
   * @return layout including text fields
   */
  public VerticalLayout createColumnTextFields() {
    VerticalLayout allColumnLayout = new VerticalLayout();
    Label description = createDescriptionLabel("Specify the columns of your file.");

    HorizontalLayout columnTFLayout = new HorizontalLayout();
    columnTFLayout.setSpacing(true);
    
    // method column
    methodColTf = new TextField("Method Column");
    methodColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    methodColTf.setImmediate(true);
    methodColTf.setValue("");
    methodColTf.setDescription("Column name of the prediction method");
    
 // taa column
    taaColTf = new TextField("TAA Column");
    taaColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    taaColTf.setImmediate(true);
    taaColTf.setValue("");
    taaColTf.setDescription(
        "Column name specifying whether the peptide is a TAA or TSA. (if not specified all peptides are assumed to be TSAs)");

    // immunogenicity column
    immColTf = new TextField("Immunogenicity Column");
    immColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    immColTf.setImmediate(true);
    immColTf.setValue("SCORE");
    immColTf.setDescription("Column name of peptide immunogenicity");
    immColTf.addValidator(new StringLengthValidator("Please enter a column name", 1, 100, true));
    immColTf.setRequired(true);

    // distance column
    distanceColTf = new TextField("Distance Column");
    distanceColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    distanceColTf.setImmediate(true);
    distanceColTf.setValue("");
    distanceColTf.setDescription("Column name of distance-to-self calculation");

    // uncertainty column
    uncertaintyColTf = new TextField("Uncertainty Column");
    uncertaintyColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    uncertaintyColTf.setImmediate(true);
    uncertaintyColTf.setValue("");
    uncertaintyColTf.setDescription("Column name of prediction uncertainty");

    // uncertainty column
    uncertaintyColTf = new TextField("Uncertainty Column");
    uncertaintyColTf.setStyleName(ValoTheme.TEXTFIELD_HUGE);
    uncertaintyColTf.setImmediate(true);
    uncertaintyColTf.setValue("");
    uncertaintyColTf.setDescription("Column name of prediction uncertainty");

    columnTFLayout.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    columnTFLayout.addComponents(methodColTf, taaColTf, distanceColTf, uncertaintyColTf, immColTf);

    allColumnLayout.addComponents(description, columnTFLayout);
    return allColumnLayout;
  }

  public VerticalLayout createHlaExpressionTextFields() {
    VerticalLayout allAlleleLayout = new VerticalLayout();
    Label description = createDescriptionLabel("Please specify the corresponding HLA-alleles and the allele expression values.");
    alleleTFLayout = new HorizontalLayout();
    HorizontalLayout alleleEVTFLayout = new HorizontalLayout();
    alleleEVTFLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    alleleTFLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);

    VerticalLayout hlaALayout = new VerticalLayout();
    Label hlaALabel = new Label("HLA-A alleles");
    hlaALayout.setSpacing(true);
    hlaALayout.setMargin(new MarginInfo(false, true, false, true));
    hlaALayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    hlaALabel.addStyleName(ValoTheme.LABEL_COLORED);
    hlaALabel.addStyleName(ValoTheme.LABEL_BOLD);
    StringLengthValidator hlaValidator = new StringLengthValidator("Please enter a valid HLA-allele");
    hlaValidator.setMinLength(11);
    hlaValidator.setMaxLength(11);
    hlaA1TF = new TextField();
    hlaA1TF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaA1TF.setImmediate(true);
    hlaA1TF.setValue("HLA-A*24:02");
    hlaA1TF.setRequired(true);
    hlaA1TF.addValidator(hlaValidator);
    hlaA1TF.setDescription("HLA-A Allele");
    hlaA2TF = new TextField();
    hlaA2TF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaA2TF.setImmediate(true);
    hlaA2TF.setValue("HLA-A*29:02");
    hlaA2TF.setDescription("HLA-A Allele");
    hlaA2TF.setRequired(true);
    hlaA2TF.addValidator(hlaValidator);
    hlaAEVTF = new TextField("HLA-A expression");
    hlaAEVTF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaAEVTF.setImmediate(true);
    hlaAEVTF.setDescription("HLA-A expression");
    hlaAEVTF.setRequired(true);
    hlaAEVTF.setConverter(new StringToDoubleConverter());
    hlaAEVTF.addValidator(new DoubleRangeValidator("Please enter a float value", 0.0, 1000.0));
    hlaAEVTF.setValue("10,0");
    hlaAEVTF.setNullSettingAllowed(false);
    hlaALayout.addComponents(hlaALabel, hlaA1TF, hlaA2TF);

    VerticalLayout hlaBLayout = new VerticalLayout();
    hlaBLayout.setSpacing(true);
    hlaBLayout.setMargin(new MarginInfo(false, true, false, true));
    Label hlaBLabel = new Label("HLA-B alleles");
    hlaBLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    hlaBLabel.addStyleName(ValoTheme.LABEL_COLORED);
    hlaBLabel.addStyleName(ValoTheme.LABEL_BOLD);
    hlaB1TF = new TextField();
    hlaB1TF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaB1TF.setImmediate(true);
    hlaB1TF.setValue("HLA-B*37:01");
    hlaB1TF.setDescription("HLA-B Allele");
    hlaB1TF.setRequired(true);
    hlaB1TF.addValidator(hlaValidator);
    hlaB2TF = new TextField();
    hlaB2TF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaB2TF.setImmediate(true);
    hlaB2TF.setValue("HLA-B*44:03");
    hlaB2TF.setDescription("HLA-B Allele");
    hlaB2TF.setRequired(true);
    hlaB2TF.addValidator(hlaValidator);
    hlaBEVTF = new TextField("HLA-B expression");
    hlaBEVTF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaBEVTF.setImmediate(true);
    hlaBEVTF.setDescription("HLA-B expression");
    hlaBEVTF.setRequired(true);
    hlaBEVTF.setConverter(new StringToDoubleConverter());
    hlaBEVTF.addValidator(new DoubleRangeValidator("Please enter a float number", 0.0, 1000.0));
    hlaBEVTF.setValue("10,0");
    hlaBEVTF.setNullSettingAllowed(false);
    hlaBLayout.addComponents(hlaBLabel, hlaB1TF, hlaB2TF);

    VerticalLayout hlaCLayout = new VerticalLayout();
    hlaCLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    hlaCLayout.setSpacing(true);
    Label hlaCLabel = new Label("HLA-C alleles");
    hlaCLayout.setMargin(new MarginInfo(false, true, false, true));
    hlaCLabel.addStyleName(ValoTheme.LABEL_COLORED);
    hlaCLabel.addStyleName(ValoTheme.LABEL_BOLD);
    hlaC1TF = new TextField();
    hlaC1TF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaC1TF.setImmediate(true);
    hlaC1TF.setValue("HLA-C*16:01");
    hlaC1TF.setDescription("HLA-C Allele");
    hlaC1TF.setRequired(true);
    hlaC1TF.addValidator(hlaValidator);
    hlaC2TF = new TextField();
    hlaC2TF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaC2TF.setImmediate(true);
    hlaC2TF.setValue("HLA-C*06:02");
    hlaC2TF.setDescription("HLA-C Allele");
    hlaC2TF.setRequired(true);
    hlaC2TF.addValidator(hlaValidator);
    hlaCEVTF = new TextField("HLA-C expression");
    hlaCEVTF.setStyleName(ValoTheme.TEXTFIELD_LARGE);
    hlaCEVTF.setImmediate(true);
    hlaCEVTF.setDescription("HLA-C expression");
    hlaCEVTF.setRequired(true);
    hlaCEVTF.setConverter(new StringToDoubleConverter());
    hlaCEVTF.addValidator(new DoubleRangeValidator("Please enter a float number", 0.0, 1000.0));
    hlaCEVTF.setValue("10,0");
    hlaCEVTF.setNullSettingAllowed(false);
    hlaCLayout.addComponents(hlaCLabel, hlaC1TF, hlaC2TF);

    alleleTFLayout.addComponents(hlaALayout, hlaBLayout, hlaCLayout);
    alleleEVTFLayout.addComponents(hlaAEVTF, hlaBEVTF, hlaCEVTF);
    alleleEVTFLayout.setSpacing(true);
    alleleEVTFLayout.setMargin(new MarginInfo(false, true, false, true));
    alleleEVTFLayout.setSizeFull();

    allAlleleLayout.addComponents(description, alleleTFLayout, alleleEVTFLayout);

    return allAlleleLayout;
  }
  
  public VerticalLayout createDataSelection() {

    VerticalLayout allDataSelectionLayout = new VerticalLayout();
    Label description = createDescriptionLabel("Welcome to the Interactive Vaccine Designer. Where is your epitope prediction file located?");
    
    HorizontalLayout dataSelectionLayout = new HorizontalLayout();
    dataSelectionLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    dataSelectionLayout.setSizeFull();
    dataSelectionLayout.setSpacing(true);

    dataSelectionUploadButton = new Button("Directory");
    dataSelectionUploadButton.setStyleName(ValoTheme.BUTTON_LARGE);
    dataSelectionUploadButton.setIcon(FontAwesome.DESKTOP);
    dataSelectionUploadButton.addClickListener((Button.ClickListener) event -> {
      useDatabase = false;
      dataLayout.setVisible(false);
      buttonLayout.setVisible(true);
      nextButton.setVisible(true);
      alleleFileUpload = false;
      fileTypeSelectionLayout.setVisible(true);
    });

    dataSelectionDatabaseButton = new Button("Database");
    dataSelectionDatabaseButton.setStyleName(ValoTheme.BUTTON_LARGE);
    dataSelectionDatabaseButton.setIcon(FontAwesome.DATABASE);
    dataSelectionDatabaseButton.addClickListener((Button.ClickListener) event -> {
      useDatabase = true;
      dataLayout.setVisible(false);
      buttonLayout.setVisible(true);
      nextButton.setVisible(false);
      alleleFileSelection.setVisible(true);
    });
    
    dataSelectionLayout.addComponents(dataSelectionUploadButton, dataSelectionDatabaseButton);
    dataSelectionLayout.setComponentAlignment(dataSelectionUploadButton, Alignment.MIDDLE_CENTER);
    dataSelectionLayout.setComponentAlignment(dataSelectionDatabaseButton, Alignment.MIDDLE_CENTER);

    allDataSelectionLayout.addComponents(description, dataSelectionLayout);
    
    return allDataSelectionLayout;
  }

  public Panel getUploadPanel() {
    return uploadPanel;
  }

  public VerticalLayout createDatabaseSelection() {
    Label description = createDescriptionLabel("Choose a file from the database and press upload.");

    projectSelectionCB = new ComboBox("Choose Project");
    projectSelectionCB.setRequired(true);
    projectSelectionCB.setFilteringMode(FilteringMode.CONTAINS);
    projectSelectionCB.setValidationVisible(true);

    alleleFileSelectionCB = new ComboBox("Choose Allele-file");
    alleleFileSelectionCB.setVisible(false);
    alleleFileSelectionCB.setRequired(true);
    alleleFileSelectionCB.setFilteringMode(FilteringMode.CONTAINS);
    NullValidator nv = new NullValidator("Please choose an allele file from the database", false);
    alleleFileSelectionCB.addValidator(nv);

    VerticalLayout databaseLayout = new VerticalLayout();

    HorizontalLayout dataBaseSelection = new HorizontalLayout();
    dataBaseSelection.setMargin(true);
    dataBaseSelection.setSpacing(true);
    dataBaseSelection.addComponents(projectSelectionCB, alleleFileSelectionCB, uploadButton);
    dataBaseSelection.setComponentAlignment(uploadButton, Alignment.BOTTOM_CENTER);


    datasetGrid = new Grid();
    datasetGrid.setSizeFull();
    datasetGrid.setVisible(false);
    datasetGrid.setImmediate(true);
    datasetGrid.setSelectionMode(SelectionMode.SINGLE);
    datasetGrid.addSelectionListener((SelectionListener) event -> {
           Object selected = ((SingleSelectionModel) datasetGrid.getSelectionModel()).getSelectedRow();
           setSelected((BeanItem<DatasetBean>) datasetGrid.getContainerDataSource().getItem(selected));
           uploadButton.setEnabled(true);
           if (alleleFileUpload) {
             alleleFileSelectionCB.setVisible(true);
           }
           if (selected == null) {
             uploadButton.setEnabled(false);
             alleleFileSelectionCB.setVisible(false);
           }
       });
    
    
    databaseLayout.addComponents(description, dataBaseSelection, datasetGrid);
    
    return databaseLayout;
  }

  public VerticalLayout createAlleleFileSelection() {

    VerticalLayout allAlleleFileSelectionLayout = new VerticalLayout();
    Label description = createDescriptionLabel("Do you want specify the allele file manually or to choose an allele file from the database later?");

    HorizontalLayout alleleFileSelectionLayout = new HorizontalLayout();
    alleleFileSelectionLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    alleleFileSelectionLayout.setSizeFull();
    alleleFileSelectionLayout.setSpacing(true);

    manualButton = new Button("Manual");
    manualButton.setStyleName(ValoTheme.BUTTON_LARGE);
    manualButton.setIcon(FontAwesome.KEYBOARD_O);
    manualButton.addClickListener((Button.ClickListener) event -> {
      this.alleleFileSelection.setVisible(false);
      this.fileTypeSelectionLayout.setVisible(true);
      this.nextButton.setVisible(true);
      alleleFileUpload = false;
    });

    databaseButton = new Button("Database");
    databaseButton.setStyleName(ValoTheme.BUTTON_LARGE);
    databaseButton.setIcon(FontAwesome.DATABASE);
    databaseButton.addClickListener((Button.ClickListener) event -> {
      this.alleleFileSelection.setVisible(false);
      alleleFileUpload = true;
      alleles = new HashMap<>();
      this.fileTypeSelectionLayout.setVisible(true);
      this.nextButton.setVisible(true);
    });

    alleleFileSelectionLayout.addComponents(manualButton, databaseButton);
    alleleFileSelectionLayout.setComponentAlignment(manualButton, Alignment.MIDDLE_CENTER);
    alleleFileSelectionLayout.setComponentAlignment(databaseButton, Alignment.MIDDLE_CENTER);

    allAlleleFileSelectionLayout.addComponents(description, alleleFileSelectionLayout);

    return allAlleleFileSelectionLayout;
  }

  public VerticalLayout createFileTypeSelection() {
    VerticalLayout fileLayout = new VerticalLayout();
    Label description = createDescriptionLabel("How is your epitope prediction file structured?");

    HorizontalLayout imagesLayout = new HorizontalLayout();
    imagesLayout.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
    HorizontalLayout colLayout = new HorizontalLayout();
    colLayout.addStyleName("clickable");
    colLayout.addStyleName("notselected");
    HorizontalLayout rowLayout = new HorizontalLayout();
    rowLayout.addStyleName("clickable");
    rowLayout.addStyleName("notselected");
    String basepath = VaadinService.getCurrent()
            .getBaseDirectory().getAbsolutePath();
    FileResource rowRe = new FileResource(new File(basepath + "/WEB-INF/images/row.png"));
    Image rowImage = new Image(null, rowRe);
    rowImage.addClickListener((MouseEvents.ClickListener) event -> {
      hlaAsColumns = false;
      immColTf.setVisible(true);
      distanceColTf.setVisible(true);
      uncertaintyColTf.setVisible(true);
      colLayout.addStyleName("notselected");
      rowLayout.removeStyleName("notselected");
    });
    FileResource colRe = new FileResource(new File(basepath + "/WEB-INF/images/column.png"));
    Image colImage = new Image(null, colRe);
    colImage.addClickListener((MouseEvents.ClickListener) event -> {
      hlaAsColumns = true;
      immColTf.setVisible(false);
      distanceColTf.setValue("");
      distanceColTf.setVisible(false);
      uncertaintyColTf.setValue("");
      uncertaintyColTf.setVisible(false);
      colLayout.removeStyleName("notselected");
      rowLayout.addStyleName("notselected");
    });
    colImage.setWidth("40%");
    rowImage.setWidth("40%");
    colLayout.addComponent(colImage);
    rowLayout.addComponent(rowImage);
    imagesLayout.addComponents(rowLayout, colLayout);
    imagesLayout.setSizeFull();


    fileLayout.addComponents(description, imagesLayout);

    return fileLayout;
  }

  public Label createDescriptionLabel(String info) {
    Label descriptionLabel = new Label(info);
    descriptionLabel.addStyleName(ValoTheme.LABEL_LARGE);
    descriptionLabel.addStyleName(ValoTheme.LABEL_BOLD);
    descriptionLabel.addStyleName("padded");

    return descriptionLabel;
  }

  public Grid getDatasetGrid() {
    return datasetGrid;
  }
  
  public TextField getImmColTf() {
    return immColTf;
  }

  public TextField getDistanceColTf() {
    return distanceColTf;
  }

  public TextField getUncertaintyColTf() {
    return uncertaintyColTf;
  }

  public TextField getTaaColTf() {
    return taaColTf;
  }
  
  public TextField getMethodColTf() {
    return methodColTf;
  }

  public UploaderInput getInputReceiver() {
    return inputReceiver;
  }

  public Upload getInputUpload() {
    return inputUpload;
  }

  public ComboBox getProjectSelectionCB() {
    return projectSelectionCB;
  }

  public Button getUploadButton() {
    return uploadButton;
  }

  public BeanItem<DatasetBean> getSelected() {
    return selected;
  }

  public void setSelected(BeanItem<DatasetBean> selected) {
    this.selected = selected;
  }

  public Button getDataSelectionDatabaseButton() {
    return dataSelectionDatabaseButton;
  }

  public Boolean getUseDatabase() {
    return useDatabase;
  }

  public Boolean getHlaAsColumns() {return hlaAsColumns;}

  public HashMap<String, String> getAlleles() {
    return alleles;
  }

  public HashMap<String, String> getAllele_expressions() {
    return allele_expressions;
  }

  public Boolean getAlleleFileUpload() {
    return alleleFileUpload;
  }

  public ComboBox getAlleleFileSelectionCB() {
    return alleleFileSelectionCB;
  }

  public void setAlleles(HashMap<String, String> alleles) {
    this.alleles = alleles;
  }

  public Boolean isHlalValid() {
    Boolean isAllValid;
    if (hlaA1TF.isValid() && hlaA2TF.isValid() && hlaB1TF.isValid() && hlaB2TF.isValid() && hlaC1TF.isValid() && hlaC2TF.isValid()) {
      isAllValid = true;
    } else {
      isAllValid = false;
    }
    return isAllValid;
  }

  public Boolean isHlaExpressionlValid() {
    Boolean isAllValid;
    if (hlaAEVTF.isValid() && hlaBEVTF.isValid() && hlaCEVTF.isValid()) {
      isAllValid = true;
    } else {
      isAllValid = false;
    }
    return isAllValid;
  }
}
