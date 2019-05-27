package com.revolut.model.responses;

import com.revolut.model.entity.AuditEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class AccountStatementResponse {
    private Map<String, AuditEntry> auditEntries;
}
