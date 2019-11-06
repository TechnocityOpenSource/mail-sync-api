package tn.technocity.mail.sync.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FolderDTO {
    private List<String> folders;
}
