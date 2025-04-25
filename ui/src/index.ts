import {definePlugin} from "@halo-dev/console-shared";
import BingeWatchingView from "./views/BingeWatching.vue";
import {markRaw} from "vue";
import MovieIcon from '~icons/icon-park-outline/movie';

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "ToolsRoot",
      route: {
        path: "/bingeWatching",
        name: "BingeWatchingRoot",
        meta: {
          title: "追剧管理",
          searchable: true,
          permissions: ["plugin:bingewatching:view"],
          menu: {
            name: "追剧管理",
            group: "content",
            icon: markRaw(MovieIcon),
            priority: 20,
          },
        },
        children: [
          {
            path: "",
            name: "BingeWatching",
            component: BingeWatchingView,
            meta: {
              title: "追剧管理",
              searchable: true,
              permissions: ["plugin:bingewatching:view"],
            },
          }
        ]
      },
    },
  ],
  extensionPoints: {},
});

