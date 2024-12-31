// Copyright 2024 Jacob Trimble
// SPDX-License-Identifier: Apache-2.0

package org.modmaker.datapack_filter_fix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ResourceLocationPattern;

/** @see net.minecraft.server.packs.resources.ResourceFilterSection */
public class NewResourceFilterSection {
  private static final Codec<NewResourceFilterSection> CODEC =
      RecordCodecBuilder.create((builder) -> {
        return builder
            .group(Codec.list(ResourceLocationPattern.CODEC)
                       .fieldOf("block")
                       .forGetter((filter) -> { return filter.blockList; }))
            .apply(builder, NewResourceFilterSection::new);
      });
  public static final MetadataSectionType<NewResourceFilterSection> TYPE =
      MetadataSectionType.fromCodec("filter", CODEC);
  private final List<ResourceLocationPattern> blockList;

  public NewResourceFilterSection(List<ResourceLocationPattern> patterns) {
    this.blockList = List.copyOf(patterns);
  }

  public boolean isNamespaceFiltered(String ns) {
    return this.blockList.stream().anyMatch(
        (pattern) -> { return pattern.namespacePredicate().test(ns); });
  }

  public boolean isFiltered(ResourceLocation location) {
    return this.blockList.stream().anyMatch(
        (pattern) -> { return pattern.locationPredicate().test(location); });
  }
}
