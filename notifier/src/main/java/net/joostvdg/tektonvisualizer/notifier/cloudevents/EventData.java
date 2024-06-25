/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.cloudevents;

import com.alibaba.fastjson2.annotation.JSONField;

public record EventData(
        @JSONField(name = "version") String version,
        @JSONField(name = "git-repo") String gitRepo,
        @JSONField(name = "git-repo-path") String gitRepoPath,
        @JSONField(name = "git-commit") String gitCommit,
        @JSONField(name = "image-url") String imageURL,
        @JSONField(name = "image-digest") String imageDigest) {}
