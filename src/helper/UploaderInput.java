package helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;

import model.EpitopeSelectionBean;

/**
 * 
 * The class {@link FileUpload} is responsible for the upload of the input data.
 * 
 * @author spaethju
 * 
 */
@SuppressWarnings("serial")
public class UploaderInput
    implements Receiver, ProgressListener, FailedListener, FinishedListener {

  private ProgressBar progress = new ProgressBar(0.0f);
  private File tempFile;
  public BeanItemContainer<EpitopeSelectionBean> beans;


  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.Receiver#receiveUpload(java.lang.String, java.lang.String)
   */
  public OutputStream receiveUpload(String filename, String mimeType) {
    try {
      tempFile = File.createTempFile("temp", ".txt");
      return new FileOutputStream(tempFile);
    } catch (IOException e) {
      Utils.notification("Upload Error!", " An error occured while uploading the data. PLease try it again.", "error");
      return null;
    }
   
  }

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.ProgressListener#updateProgress(long, long)
   */
  @Override
  public void updateProgress(long readBytes, long contentLength) {
    progress.setVisible(true);
  }

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.FailedListener#uploadFailed(com.vaadin.ui.Upload.FailedEvent)
   */
  @Override
  public void uploadFailed(FailedEvent event) {
    Utils.notification("Upload failed!", "An problem occured while uploading the file. Please try again or make sure your file is an acceptable TSV-file", "error");
    progress.setVisible(false);
  }

  /**
   * @return progress bar with the actual state of the upload
   */
  public ProgressBar getProgress() {
    return progress;
  }
  
  /**
   * @return uploaded file temporarly stored in
   */
  public File getTempFile() {
    return tempFile;
  }

  /* (non-Javadoc)
   * @see com.vaadin.ui.Upload.FinishedListener#uploadFinished(com.vaadin.ui.Upload.FinishedEvent)
   */
  @Override
  public void uploadFinished(FinishedEvent event) {
    
  }
}

