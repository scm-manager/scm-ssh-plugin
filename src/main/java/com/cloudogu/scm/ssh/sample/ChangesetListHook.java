package com.cloudogu.scm.ssh.sample;

import com.github.legman.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.api.HookChangesetBuilder;

@Extension
@EagerSingleton
public class ChangesetListHook {

  private static final Logger LOG = LoggerFactory.getLogger(ChangesetListHook.class);

  @Subscribe
  public void handle(PostReceiveRepositoryHookEvent event) {
    HookChangesetBuilder changesetProvider = event.getContext().getChangesetProvider();
    for (Changeset changeset : changesetProvider.getChangesets()) {
      LOG.warn("received changeset {}", changeset.getId());
    }
  }

}
