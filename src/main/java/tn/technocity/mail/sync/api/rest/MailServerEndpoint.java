package tn.technocity.mail.sync.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.technocity.mail.sync.api.dto.AccountDTO;
import tn.technocity.mail.sync.api.dto.ActionDTO;
import tn.technocity.mail.sync.api.dto.FolderDTO;
import tn.technocity.mail.sync.api.service.MailCopyService;
import tn.technocity.mail.sync.api.service.MailServerService;

import javax.mail.*;
import java.util.List;

@RestController
@RequestMapping("mail-server")
public class MailServerEndpoint {

    @Autowired
    private MailServerService mailServerService;

    @Autowired
    private MailCopyService mailCopyService;

    /**
     * Connect to a mail server
     *
     * @return
     * @throws MessagingException
     */
    @PostMapping("connect")
    private ResponseEntity<?> connect(@RequestBody AccountDTO accountDTO) throws MessagingException {
        return ResponseEntity.ok(mailServerService.connect(accountDTO));
    }

    /**
     * Get All folders in Mail Box
     *
     * @param accountDTO
     * @return
     * @throws MessagingException
     */
    @PostMapping("folder-list")
    private ResponseEntity<List<FolderDTO>> getAllFolders(@RequestBody AccountDTO accountDTO) throws MessagingException {
        return ResponseEntity.ok(mailServerService.getFolders(accountDTO));
    }

    /**
     * Copy mail folder from source  to target
     *
     * @param action
     * @return
     * @throws MessagingException
     */
    @PostMapping("/copy")
    public ResponseEntity<Void> copy(@RequestBody ActionDTO action) throws MessagingException {
        mailCopyService.copy(action);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/copyAll")
    public ResponseEntity<Void> copyAll (@RequestBody ActionDTO action) throws MessagingException {
        mailCopyService.copyAll(action);
        return ResponseEntity.ok().build();
    }
}
