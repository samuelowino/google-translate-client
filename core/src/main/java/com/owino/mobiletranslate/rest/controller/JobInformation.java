package com.owino.mobiletranslate.rest.controller;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Represents information about a job or a task to be accomplished
 * @param future this is a result of an asynchronous operation of type CompletableFuture<T> not Future
 * @param startTime start of our job or task
 */
public record JobInformation(CompletableFuture<Object> future, Instant startTime) {
}
