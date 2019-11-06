package tn.technocity.mail.sync.api.service;

import com.sun.mail.imap.IMAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.technocity.mail.sync.api.dto.AccountDTO;
import tn.technocity.mail.sync.api.dto.FolderDTO;
import tn.technocity.mail.sync.api.rest.MailServerEndpoint;

import javax.mail.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class MailServerService {

    private static Logger logger = LoggerFactory.getLogger(MailServerEndpoint.class);

    public boolean connect(AccountDTO accountDTO) throws MessagingException {
        connectAndGetStore(accountDTO);
        return true;
    }

    public List<FolderDTO> getFolders(AccountDTO accountDTO) throws MessagingException {
        Store store = connectAndGetStore(accountDTO);
        Folder[] folders = store.getDefaultFolder().list();
        logger.info("Mail folders have been successfully loaded ...");
        // TODO: get folders size + get all subFolders (with recursivity)
        return Arrays.asList(folders).stream().map(folder -> FolderDTO.builder()
                .folderName(folder.getFullName())
                .size(getFolderSize(folder))
                .build()).collect(Collectors.toList());
    }

    private Store connectAndGetStore(AccountDTO accountDTO) throws MessagingException {

        logger.info("1st ===> setup Mail Server Properties..");
        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", accountDTO.getPort());
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        logger.info("Mail Server Properties have been setup successfully..");

        logger.info("2nd ===> get Mail Session..");
        Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        logger.info("Mail Session has been created successfully..");

        Store store = getMailSession.getStore("imaps");

        store.connect(accountDTO.getHost(), accountDTO.getUser(), accountDTO.getPassword());

        return store;
    }

    /**
     * Get size of a Folder
     *
     * @param folder
     * @return
     * @throws MessagingException
     */
    private long getFolderSize(Folder folder) {
        long msgsSize = 0;
        try {
            if (folder.getName().length() != 0) { //root folder
                if (folder.getMessageCount() != 0) {
                    logger.info("folder.getMessageCount() " + folder.getMessageCount());
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.getMessages();
                    logger.info("messages.length " + messages.length);
                    logger.info("msgs_size " + msgsSize);
                    for (int i = 0; i < messages.length; i++) {
                        IMAPMessage tmp = (IMAPMessage) messages[i];
                        if (tmp.getSize() != -1) {
                            logger.info("tmp.getSize() " + tmp.getSize());
                            msgsSize += tmp.getSize();
                            logger.info("msgs_size " + msgsSize); //size in bytes
                            logger.info(String.valueOf(msgsSize / (1024L * 1024L))); //converted to MB
                        }
                    }
                }

            }
        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
        }

        return msgsSize;
    }
}
