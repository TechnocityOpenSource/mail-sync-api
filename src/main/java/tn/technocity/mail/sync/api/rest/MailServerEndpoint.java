package tn.technocity.mail.sync.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.technocity.mail.sync.api.dto.AccountDTO;
import tn.technocity.mail.sync.api.dto.FolderDTO;
import tn.technocity.mail.sync.api.service.MailServerService;

import javax.mail.*;
import java.util.List;

@RestController
@RequestMapping("mail-server")
public class MailServerEndpoint {

    @Autowired
    private MailServerService mailServerService;

    /**
     * Connect to a mail server
     *
     * @return
     * @throws MessagingException
     */
    @PostMapping
    private ResponseEntity<?> connect(@RequestBody AccountDTO accountDTO) throws MessagingException {
        return ResponseEntity.ok(mailServerService.connect(accountDTO));
    }

    /**
     * Get All folders in Mail Box
     *
     * @param accountDTO
     * @throws MessagingException
     * @return
     */
    @GetMapping
    private ResponseEntity<List<FolderDTO>> getAllFolders(@RequestBody AccountDTO accountDTO) throws MessagingException {
        return ResponseEntity.ok(mailServerService.getFolders(accountDTO));
    }
}
