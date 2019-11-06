package tn.technocity.mail.sync.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tn.technocity.mail.sync.api.dto.AccountDTO;
import tn.technocity.mail.sync.api.dto.FolderDTO;
import tn.technocity.mail.sync.api.rest.MailServerEndpoint;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class MailServerService {

    private static Logger logger = LoggerFactory.getLogger(MailServerEndpoint.class);

    public boolean connect(AccountDTO accountDTO) throws MessagingException {
        connectAndGetStore(accountDTO);
        return true;
    }

    public FolderDTO getFolders(AccountDTO accountDTO) throws MessagingException {
        Store store = connectAndGetStore(accountDTO);
        Folder[] folders = store.getDefaultFolder().list();
        logger.info("Mail folders have been successfully loaded ...");
        // TODO: get folders size + get all subFolders (with recursivity)
        return new FolderDTO(Arrays.asList(folders).stream().map(Folder::getFullName).collect(Collectors.toList()));
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
}
