import {definePlugin, type CommentSubjectRefResult, type CommentSubjectRefProvider } from "@halo-dev/console-shared";
import BingeWatchingView from "./views/BingeWatching.vue";
import {markRaw} from "vue";
import MovieIcon from '~icons/icon-park-outline/movie';
import type { Extension } from "@halo-dev/api-client";
import {type HandsomeMovie} from "@/api/generated";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "ToolsRoot",
      route: {
        path: "/bingeWatching",
        name: "BingeWatchingRoot",
        meta: {
          title: "海报墙管理",
          searchable: true,
          permissions: ["plugin:bingewatching:view"],
          menu: {
            name: "海报墙管理",
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
              title: "海报墙管理",
              searchable: true,
              permissions: ["plugin:bingewatching:view"],
            },
          }
        ]
      },
    },
  ],
  extensionPoints: {
    "comment:subject-ref:create": (): CommentSubjectRefProvider[] => {
      return [
        {
          kind: "HandsomeMovie",
          group: "bingewatching.lik.cc",
          resolve: (subject: Extension): CommentSubjectRefResult => {
            const handsomeMovie = subject as HandsomeMovie;
            return {
              label: "海报墙",
              title: `${handsomeMovie.spec.vod_name}  ${handsomeMovie.spec.status}`,
              externalUrl: `/movies/${handsomeMovie.metadata.name}`,
              route: {
                name: "BingeWatching",
              },
            };
          },
        },
      ];
    },
  },
});

