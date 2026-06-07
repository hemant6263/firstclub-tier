package com.firstclub.interview.dto;

import java.time.Instant;

public record ApiMeta(String requestId, Instant timestamp, String apiVersion) {}
