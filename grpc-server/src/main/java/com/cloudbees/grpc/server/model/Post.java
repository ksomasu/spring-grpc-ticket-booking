package com.cloudbees.grpc.server.model;

public record Post(int id, String title, String content, Integer releaseYear, int authorId) {}