# DataPack Filter Fix

Minecraft has data packs, which allow changing things like recipes without
writing a mod.  In the data pack, you can set a `filter` property to remove
files from data packs that appear before it.

Consider this example.  This should remove the `non_existant_path` from the
`minecraft` namespace and all the recipes from the `non_existant_mod`.

```json
{
  "pack": {
    "description": {
      "text": "Test pack to reproduce filter error"
    },
    "pack_format": 15
  },
  "filter": {
    "block": [
      {"namespace": "minecraft", "path": "non_existant_path"},
      {"namespace": "non_existant_mod", "path": "recipes"}
    ]
  }
}
```

However, in Vanilla, it will handle the namespaces and paths separately.  This
will cause it to apply both path filters to both namespaces.  So in reality, it
will remove all `minecraft` recipes too.

This mod fixes the handling of the `filter` attribute so it works as you'd
expect.
