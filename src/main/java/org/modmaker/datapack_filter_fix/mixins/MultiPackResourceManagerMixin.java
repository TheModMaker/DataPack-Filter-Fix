// Copyright 2024 Jacob Trimble
// SPDX-License-Identifier: Apache-2.0

package org.modmaker.datapack_filter_fix.mixins;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.modmaker.datapack_filter_fix.NewResourceFilterSection;

/** @see net.minecraft.server.packs.resources.MultiPackResourceManager */
@Mixin(MultiPackResourceManager.class)
public abstract class MultiPackResourceManagerMixin {

  @Shadow private Map<String, FallbackResourceManager> namespacedManagers;

  @Shadow private List<PackResources> packs;

  @SuppressWarnings("null")
  @Inject(method = "<init>", at = @At("RETURN"))
  private void onInit(PackType pack_type, List<PackResources> resources,
                      CallbackInfo ci) {
    // Clear out the original handles to re-create them.
    for (String entry : this.namespacedManagers.keySet()) {
      this.namespacedManagers.put(
          entry, new FallbackResourceManager(pack_type, entry));
    }

    // Copied from original implementation, but with our fix.  The resource
    // location filter needs to check both the namespace and the path.
    List<String> all_namespaces =
        this.packs.stream()
            .flatMap(
                (pack) -> { return pack.getNamespaces(pack_type).stream(); })
            .distinct()
            .toList();

    for (PackResources pack : this.packs) {
      NewResourceFilterSection filters = this.getNewPackFilterSection(pack);
      Set<String> pack_namespaces = pack.getNamespaces(pack_type);
      Predicate<ResourceLocation> predicate = filters != null ? (location) -> {
        return filters.isFiltered(location);
      } : null;

      for (String ns : all_namespaces) {
        boolean has_ns_files = pack_namespaces.contains(ns);
        boolean has_filters =
            filters != null && filters.isNamespaceFiltered(ns);
        if (has_ns_files || has_filters) {
          FallbackResourceManager manager = this.namespacedManagers.get(ns);
          if (has_ns_files && has_filters) {
            manager.push(pack, predicate);
          } else if (has_ns_files) {
            manager.push(pack);
          } else {
            manager.pushFilterOnly(pack.packId(), predicate);
          }
        }
      }
    }
  }

  @Nullable
  private NewResourceFilterSection getNewPackFilterSection(PackResources pack) {
    try {
      return pack.getMetadataSection(NewResourceFilterSection.TYPE);
    } catch (IOException ioexception) {
      return null;
    }
  }
}
