package com.layer.gradle.gitrepo

import org.ajoberstar.grgit.Grgit
import org.eclipse.jgit.api.ResetCommand

final class GrgitHelper {
    private GrgitHelper() {
        throw new AssertionError("No instances")
    }

    static File ensureLocalRepo(File directory, String name, String gitUrl, String branch, boolean createLocalBranch) {
        File repoDir = new File(directory, name)
        Grgit git = repoDir.directory
            ? Grgit.open(dir: repoDir)
            : Grgit.clone(dir: repoDir, uri: gitUrl, checkout: false)

        try {
            git.fetch()
            checkoutRemoteCommit(git, branch, createLocalBranch)
            return repoDir
        } finally {
            git.close()
        }
    }

    private static void checkoutRemoteCommit(Grgit git, String branch, boolean createLocalBranch) {
        def jgit = git.repository.jgit
        def repository = jgit.repository
        def remoteCommit = repository.resolve("refs/remotes/origin/${branch}")
        if (remoteCommit == null) {
            throw new IllegalStateException("Remote branch origin/${branch} not found")
        }

        if (createLocalBranch) {
            if (repository.findRef("refs/heads/${branch}") != null) {
                jgit.checkout().setName(branch).call()
                jgit.reset().setMode(ResetCommand.ResetType.HARD).setRef(remoteCommit.name).call()
            } else {
                jgit.checkout().setCreateBranch(true).setName(branch).setStartPoint(remoteCommit.name).call()
            }
        } else {
            jgit.checkout().setName(remoteCommit.name).call()
        }
    }
}
