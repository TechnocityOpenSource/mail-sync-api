package tn.technocity.mail.sync.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tn.technocity.mail.sync.api.dto.ActionDTO;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;


@Service
public class MailCopyService {

    @Autowired
    private MailServerService mailServerService;

    /**
     * Copy Folder from a source account to another (also support Folder which contain subfolders)
     *
     * @param action
     * @throws MessagingException
     */
    public void copy(ActionDTO action) throws MessagingException {

        if (StringUtils.isEmpty(action.getSourceFolder())) {
            throw new RuntimeException("Source folder is empty");
        }

        if (StringUtils.isEmpty(action.getTargetFolder())) {
            throw new RuntimeException("Target folder is empty");
        }

        Store sourceStore = mailServerService.connectAndGetStore(action.getAccountSource());
        Store targetStore = mailServerService.connectAndGetStore(action.getAccountTarget());

        copyMailsAndFolders(sourceStore, action.getSourceFolder(), targetStore.getDefaultFolder(), action.getTargetFolder());
    }

    /**
     * Copy all Folder from a source account to a target account
     *
     * @param action
     * @throws MessagingException
     */
    public void copyAll(ActionDTO action) throws MessagingException {

        Store sourceStore = mailServerService.connectAndGetStore(action.getAccountSource());
        Store targetStore = mailServerService.connectAndGetStore(action.getAccountTarget());

        for (Folder sourceFolder : sourceStore.getDefaultFolder().list()) {
            copyMailsAndFolders(sourceStore, sourceFolder.getName(), targetStore.getDefaultFolder(), sourceFolder.getName());
        }

    }

    /**
     * Recursively copy mails and subfolders
     *
     * @param sourceStore
     * @param sourceFolderName
     * @param targetParent
     * @param targetFolderName
     * @throws MessagingException
     */
    private void copyMailsAndFolders(Store sourceStore, String sourceFolderName, Folder targetParent, String targetFolderName) throws MessagingException {

        Folder sourceFolder = sourceStore.getFolder(sourceFolderName);
        Folder targetFolder = getOrCreateFolder(targetParent, targetFolderName);

        if (sourceFolder.getMessageCount() != 0) {
            copyMessages(sourceFolder, targetFolder);
        }

        for (Folder subFolderSource : sourceStore.getFolder(sourceFolder.getFullName()).list()) {
            copyMailsAndFolders(sourceStore, subFolderSource.getFullName(), targetFolder, subFolderSource.getName());
        }

    }

    /**
     * Copy mails from a root folder of a source account to a root folder of a target account
     *
     * @param targetFolder
     * @throws MessagingException
     */
    private void copyMessages(Folder sourceFolder, Folder targetFolder) throws MessagingException {
        sourceFolder.open(Folder.READ_ONLY);
        sourceFolder.copyMessages(sourceFolder.getMessages(), targetFolder);
    }

    /**
     * get folder if it is already existed; create folder if it does not exist
     *
     * @param folderName
     * @return
     * @throws MessagingException
     */
    private Folder getOrCreateFolder(Folder parent, String folderName) throws MessagingException {
        Folder targetFolder = parent.getFolder(folderName);
        if (!targetFolder.exists()) {
            createFolder(parent, folderName);
        }
        return targetFolder;
    }

    /**
     * Create Folder respecting hierarchy
     *
     * @param parent
     * @param folderName
     * @return
     * @throws MessagingException
     */
    private Folder createFolder(Folder parent, String folderName) throws MessagingException {
        Folder newFolder = parent.getFolder(folderName);
        if (newFolder.create(Folder.HOLDS_MESSAGES | Folder.HOLDS_FOLDERS)) {
            newFolder.setSubscribed(true);
            return parent.getFolder(folderName);
        } else {
            throw new RuntimeException("Cannot create folder " + folderName + " in destination mail box.");
        }
    }


}
