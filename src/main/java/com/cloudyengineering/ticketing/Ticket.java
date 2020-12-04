package com.cloudyengineering.ticketing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticket {

    @JsonProperty("id")
    private Long ticketId;

    private String summary;

    private String description;

    private String assigneeLink;

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssigneeLink() {
        return assigneeLink;
    }

    public void setAssigneeLink(String assigneeLink) {
        this.assigneeLink = assigneeLink;
    }
}
