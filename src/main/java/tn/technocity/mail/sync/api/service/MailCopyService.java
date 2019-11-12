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

    public void copy(ActionDTO action) throws MessagingException {

        if (StringUtils.isEmpty(action.getSourceFolder())) {
            throw new RuntimeException("Source folder is empty");
        }

        if (StringUtils.isEmpty(action.getTargetFolder())) {
            throw new RuntimeException("Target folder is empty");
        }

        Store sourceStore = mailServerService.connectAndGetStore(action.getAccountSource());
        Store targetStore = mailServerService.connectAndGetStore(action.getAccountTarget());

        Folder sourceFolder = sourceStore.getFolder(action.getSourceFolder());
        sourceFolder.open(Folder.READ_ONLY);

        Folder targetFolder = getOrCreateFolder(targetStore, action.getTargetFolder());
        targetFolder.open(Folder.READ_WRITE);



        sourceFolder.copyMessages(sourceFolder.getMessages(), targetFolder);

        // Folder destinationFolder = createFolder(storeOfDestinationMail, storeOfDestinationMail.getDefaultFolder(), action.getFolderName());
/*
        if (subfolderOfOriginalMail == null) {
            //folder without subfolders
            Message[] messages = folderOfOriginMail.getMessages();
            folderOfOriginMail.copyMessages(messages, destinationFolder);
        } else {
            //folder with subfolders
            for (Folder folder : subfolderOfOriginalMail) {
                String subfolderName = folder.getName();
                Folder destinationSubFolder = createFolder(storeOfDestinationMail, destinationFolder, subfolderName);
                Message[] messages = storeOfOriginMail.getFolder(subfolderName).getMessages();
                storeOfOriginMail.getFolder(subfolderName).copyMessages(messages, destinationSubFolder);
            }
        }
 */
    }

    private Folder getOrCreateFolder(Store targetStore, String targetFolderName) throws MessagingException {
        Folder targetFolder = targetStore.getFolder(targetFolderName);
        if (!targetFolder.exists()) {
            createFolder(targetStore, targetStore.getDefaultFolder(), targetFolderName);
        }
        return targetFolder;
    }

    /**
     * Create if not exist
     *
     * @param store
     * @param folder
     * @param folderName
     * @return
     * @throws MessagingException
     */
    private Folder createFolder(Store store, Folder folder, String folderName) throws MessagingException {
        if (folder.getParent() == null) {
            Folder defaultFolder = store.getDefaultFolder();
            return create(store, defaultFolder, folderName);
        } else {
            Folder defaultFolder = store.getFolder(folder.getName());
            return create(store, defaultFolder, folderName);
        }
    }

    /*
     * Note that in Gmail folder hierarchy is not maintained.
     * */
    private Folder create(Store store, Folder parent, String folderName) throws MessagingException {
        Folder newFolder = parent.getFolder(folderName);
        if (newFolder.create(Folder.HOLDS_MESSAGES | Folder.HOLDS_FOLDERS)) {
            newFolder.setSubscribed(true);
            return store.getFolder(folderName);
        } else {
            throw new RuntimeException("Cannot create folder " + folderName + " in destination mail box.");
        }
    }
}
