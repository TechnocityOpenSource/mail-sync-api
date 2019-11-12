package tn.technocity.mail.sync.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionDTO {

    @JsonProperty("source")
    private AccountDTO accountSource;

    @JsonProperty("target")
    private AccountDTO accountTarget;

    private String sourceFolder;

    private String targetFolder;
}
