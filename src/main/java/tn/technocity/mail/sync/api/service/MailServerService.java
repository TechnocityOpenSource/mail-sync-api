package tn.technocity.mail.sync.api.service;

import com.sun.mail.imap.IMAPMessage;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class MailServerService {

    /**
     * Connect to MailBox
     * @param accountDTO
     * @return
     * @throws MessagingException
     */
    public boolean connect(AccountDTO accountDTO) throws MessagingException {
        connectAndGetStore(accountDTO);
        return true;
    }

    /**
     * Connect to MailBox and get folder and sub folder list
     *
     * @param accountDTO
     * @return
     * @throws MessagingException
     */
    public List<FolderDTO> getFolders(AccountDTO accountDTO) throws MessagingException {
        Store store = connectAndGetStore(accountDTO);
        log.info("Load Mail folders..");
        Folder[] folders = store.getDefaultFolder().list("*");
        log.info("Mail folders have been successfully loaded..");
        return Arrays.asList(folders).stream().map(folder -> FolderDTO.builder()
                .folderName(folder.getFullName())
                .size(getFolderSize(folder))
                .build()).collect(Collectors.toList());
    }

    protected Store connectAndGetStore(AccountDTO accountDTO) throws MessagingException {

        log.info("Setup Mail Server Properties..");
        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", accountDTO.getPort());
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        log.info("Mail Server Properties have been setup successfully..");

        log.info("Get Mail Session..");
        Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        log.info("Mail Session has been created successfully..");

        Store store = getMailSession.getStore("imaps");

        log.info("Connect to MailBox..");
        store.connect(accountDTO.getHost(), accountDTO.getUser(), accountDTO.getPassword());
        log.info("Connected to MailBox successfully..");

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
                    log.info("folder.getMessageCount() " + folder.getMessageCount());
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.getMessages();
                    log.info("messages.length " + messages.length);
                    log.info("msgs_size " + msgsSize);
                    for (int i = 0; i < messages.length; i++) {
                        IMAPMessage tmp = (IMAPMessage) messages[i];
                        if (tmp.getSize() != -1) {
                            log.info("tmp.getSize() " + tmp.getSize());
                            msgsSize += tmp.getSize();
                            log.info("msgs_size " + msgsSize); //size in bytes
                            log.info(String.valueOf(msgsSize / (1024L * 1024L))); //converted to MB
                        }
                    }
                }

            }
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }

        return msgsSize;
    }
}
