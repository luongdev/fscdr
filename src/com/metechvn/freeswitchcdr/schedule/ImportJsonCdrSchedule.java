package com.metechvn.freeswitchcdr.schedule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metechvn.freeswitchcdr.messages.JsonCdrMessage;
import com.metechvn.freeswitchcdr.services.JsonCdrStoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

@Component
@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class ImportJsonCdrSchedule {

    private final ObjectMapper om;
    private final File jsonCdrDir;
    private final File jsonCdrBackupDir;
    private final boolean removedAfterImport;
    private final JsonCdrStoreService jsonCdrStoreService;

    public ImportJsonCdrSchedule(
            ObjectMapper om,
            JsonCdrStoreService jsonCdrStoreService,
            @Value("${app.json-cdr.dir}") String jsonCdrDir,
            @Value("${app.json-cdr.backup-dir:}") String jsonCdrBackupDir,
            @Value("${app.json-cdr.remove-after-import:false}") boolean removedAfterImport) throws IOException {
        this.om = om;
        this.removedAfterImport = removedAfterImport;
        this.jsonCdrStoreService = jsonCdrStoreService;
        this.jsonCdrDir = Paths.get(jsonCdrDir).toFile();
        if (!this.jsonCdrDir.exists() || !this.jsonCdrDir.isDirectory())
            throw new IOException("Cannot access json_cdr directory " + jsonCdrDir);

        if (StringUtils.isEmpty(jsonCdrBackupDir)) {
            this.jsonCdrBackupDir = Paths.get(jsonCdrDir, "backup").toFile();
        } else {
            this.jsonCdrBackupDir = Paths.get(jsonCdrBackupDir).toFile();
        }

        if (this.jsonCdrBackupDir.exists()) {
            if (!this.jsonCdrBackupDir.canWrite())
                throw new IOException("Cannot write to json_cdr backup dir " + jsonCdrBackupDir);

            return;
        }

        Files.createDirectory(this.jsonCdrBackupDir.toPath());
    }

    private final FileFilter JSON_CDR_FILE_FILTER = f -> f.getName().endsWith(".cdr.json") && f.canRead();
    private final Comparator<File> JSON_CDR_FILE_SORT = (f1, f2) -> {
        try {
            var f1Created = Files.readAttributes(f1.toPath(), BasicFileAttributes.class).creationTime().toMillis();
            var f2Created = Files.readAttributes(f2.toPath(), BasicFileAttributes.class).creationTime().toMillis();

            return Long.compare(f1Created, f2Created);
        } catch (Exception e) {
            return 0;
        }
    };

    @Scheduled(cron = "${app.json-cdr.import-cron:0/5 * * * * *}")
    public void scanJsonCdrFiles() throws IOException {
        var jsonFiles = this.jsonCdrDir.listFiles(JSON_CDR_FILE_FILTER);
        if (jsonFiles == null || jsonFiles.length == 0) {

            return;
        }

        var sortedJsonFiles = Arrays.stream(jsonFiles).sorted(JSON_CDR_FILE_SORT).toList();
        for (var f : sortedJsonFiles) {
            var map = om.readValue(f, new TypeReference<Map<String, Object>>() {
            });

            if (map.containsKey("variables") && map.get("variables") instanceof Map variables) {
                if (variables.containsKey("uuid") && variables.get("uuid") instanceof String uuid) {
                    var globalCallId = uuid;
                    if (variables.containsKey("global_call_id")
                            && variables.get("global_call_id") instanceof String callId) globalCallId = callId;
                    else if (variables.containsKey("sip_h_X-Call-Id")
                            && variables.get("sip_h_X-Call-Id") instanceof String callId) globalCallId = callId;

                    jsonCdrStoreService.store(new JsonCdrMessage(uuid, globalCallId, map));

                    if (removedAfterImport) {
                        Files.deleteIfExists(f.toPath());
                    } else {
                        Files.move(f.toPath(), Paths.get(this.jsonCdrBackupDir.getPath(), f.getName()));
                    }
                }
            }
        }
    }
}