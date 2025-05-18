<script lang="ts" setup>
import {
  Dialog,
  IconAddCircle,
  IconCloseCircle,
  IconRefreshLine,
  Toast,
  VButton,
  VCard,
  VEmpty,
  VLoading,
  VPageHeader,
  VPagination,
  VSpace
} from "@halo-dev/components";
import {useQuery} from "@tanstack/vue-query";
import {computed, onMounted, ref, watch} from "vue";
import {useRouteQuery} from "@vueuse/router";
import MovieEditingModal from "@/components/MovieEditingModal.vue";
import MovieSyncModal from "@/components/MovieSyncModal.vue";
import MovieQuotesModal from "@/components/MovieQuotesModal.vue";
import {handsomeMovieApi} from "@/api";
import type {HandsomeMovie, HandsomeMovieList} from "@/api/generated";
import IconParkMovie from '~icons/icon-park-outline/movie';
import IconParkEdit from '~icons/icon-park-outline/edit';
import IconParkMessage from '~icons/icon-park-outline/message';
import IconParkHelp from '~icons/icon-park-outline/help';
import IconParkPlus from '~icons/icon-park-outline/plus';
import IconParkMinus from '~icons/icon-park-outline/minus';

defineOptions({
  name: "MovieView",
});

const selectedMovie = ref<HandsomeMovie | undefined>();
const selectedMovies = ref<string[]>([]);
const checkedAll = ref(false);
const selectedSort = useRouteQuery<string | undefined>("sort");
const selectedType = useRouteQuery<string | undefined>("status");
const movieStatus = ref<{ label: string; value: string | undefined; }[]>([
  { label: "全部", value: undefined },
  { label: "观看中", value: "观看中" },
  { label: "完结", value: "完结" },
  { label: "弃坑", value: "弃坑" },
]);

const page = useRouteQuery<number>("page", 1, {
  transform: Number,
});
const size = useRouteQuery<number>("size", 20, {
  transform: Number,
});
const keyword = useRouteQuery<string>("keyword", "");
const searchText = ref("");
const total = ref(0);
const editingModal = ref(false);
const syncModal = ref(false);
const quotesModal = ref(false);

watch(
  () => [selectedSort.value, selectedType.value, keyword.value],
  () => {
    page.value = 1;
  }
);

function handleClearFilters() {
  selectedSort.value = undefined;
  selectedType.value = undefined;
}

const hasFilters = computed(() => {
  return selectedSort.value || selectedType.value;
});

const {
  data: movies,
  isLoading,
  isFetching,
  refetch,
} = useQuery({
  queryKey: ["movies", page, size, selectedSort, selectedType, keyword],
  queryFn: async () => {
    try {
      const response = await handsomeMovieApi.listHandsomeMovies({
        page: page.value,
        size: size.value,
        sort: selectedSort.value ? [selectedSort.value] : undefined,
        type: selectedType.value,
        keyword: keyword.value,
      });
      return response;
    } catch (error) {
      console.error("Failed to fetch Movies:", error);
      Toast.error("获取海报墙信息列表失败");
      return {
        items: [],
        total: 0,
        page: page.value,
        size: size.value
      } as HandsomeMovieList;
    }
  },
  keepPreviousData: true,
  onSuccess: (data: HandsomeMovieList) => {
    total.value = data.total;
  }
});

const handleCheckAllChange = (e: Event) => {
  const { checked } = e.target as HTMLInputElement;
  checkedAll.value = checked;
  if (checkedAll.value) {
    selectedMovies.value =
      movies.value?.items.map((movie) => movie.metadata.name) || [];
  } else {
    selectedMovies.value.length = 0;
  }
};

const handleDeleteInBatch = () => {
  Dialog.warning({
    title: "是否确认删除所选的时间线？",
    description: "删除之后将无法恢复。",
    confirmType: "danger",
    onConfirm: async () => {
      try {
        const promises = selectedMovies.value.map((movie) => {
          return handsomeMovieApi.deleteHandsomeMovie(movie);
        });
        if (promises) {
          await Promise.all(promises);
        }
        selectedMovies.value.length = 0;
        checkedAll.value = false;
        Toast.success("删除成功");
        refetch();
      } catch (e) {
        console.error(e);
      } finally {
        refetch();
      }
    },
  });
};

function handleReset() {
  keyword.value = "";
  searchText.value = "";
}

function onKeywordChange() {
  keyword.value = searchText.value;
}

const handleOpenCreateModal = () => {
  selectedMovie.value = undefined;
  editingModal.value = true;
};

const handleEditMovie = (movie: HandsomeMovie) => {
  selectedMovie.value = movie;
  editingModal.value = true;
};

const onEditingModalClose = async () => {
  selectedMovie.value = undefined;
  refetch();
};

const handleOpenSyncModal = () => {
  syncModal.value = true;
};

const onSyncModalClose = async () => {
  refetch();
};

const handleOpenQuotesModal = (movie: HandsomeMovie) => {
  selectedMovie.value = movie;
  quotesModal.value = true;
};

const onQuotesModalClose = async () => {
  selectedMovie.value = undefined;
  refetch();
};

const handleIncreaseSeen = async (movie: HandsomeMovie) => {
  try {
    const currentSeen = Number(movie.spec.seen) || 0;
    const currentNewSeen = Number(movie.spec.newSeen) || 0;
    const newSeen = currentSeen + 1;
    const newNewSeen = currentNewSeen > 0 ? currentNewSeen - 1 : 0;
    await handsomeMovieApi.updateHandsomeMovie(
      movie.metadata.name,
      {
        ...movie,
        spec: {
          ...movie.spec,
          seen: newSeen.toString(),
          newSeen: newNewSeen.toString()
        }
      }
    );
    Toast.success("更新成功");
    refetch();
  } catch (error) {
    console.error("Failed to update movie:", error);
    Toast.error("更新失败");
  }
};

const handleDecreaseSeen = async (movie: HandsomeMovie) => {
  try {
    const currentSeen = Number(movie.spec.seen) || 0;
    const currentNewSeen = Number(movie.spec.newSeen) || 0;
    if (currentSeen <= 0) {
      Toast.warning("已看集数不能小于0");
      return;
    }
    const newSeen = currentSeen - 1;
    const newNewSeen = currentNewSeen + 1;
    await handsomeMovieApi.updateHandsomeMovie(
      movie.metadata.name,
      {
        ...movie,
        spec: {
          ...movie.spec,
          seen: newSeen.toString(),
          newSeen: newNewSeen.toString()
        }
      }
    );
    Toast.success("更新成功");
    refetch();
  } catch (error) {
    console.error("Failed to update movie:", error);
    Toast.error("更新失败");
  }
};

onMounted(() => {
  // No need to fetch movie status as it's no longer fetched
});
</script>

<template>
  <MovieEditingModal
    v-model:visible="editingModal"
    :movie="selectedMovie"
    @close="onEditingModalClose"
  />
  <MovieSyncModal
    v-model:visible="syncModal"
    @close="onSyncModalClose"
  />
  <MovieQuotesModal
    v-model:visible="quotesModal"
    :movie="selectedMovie"
    @close="onQuotesModalClose"
  />

  <VPageHeader title="影视管理">
    <template #icon>
      <IconParkMovie />
    </template>
    <template #actions>
      <VSpace v-permission="['plugin:bingewatching:manage']">
        <VButton
          type="secondary"
          @click="handleOpenSyncModal"
        >
          <template #icon>
            <IconRefreshLine class="h-full w-full" />
          </template>
          同步影视
        </VButton>
        <VButton
          type="secondary"
          @click="handleOpenCreateModal"
        >
          <template #icon>
            <IconAddCircle class="h-full w-full" />
          </template>
          新建
        </VButton>
      </VSpace>
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-0']">
      <template #header>
        <div class="block w-full bg-gray-50 px-4 py-3">
          <div class="relative flex flex-col flex-wrap items-start gap-4 sm:flex-row sm:items-center">
            <div
              v-permission="['plugin:bingewatching:manage']"
              class="hidden items-center sm:flex"
            >
              <input
                v-model="checkedAll"
                type="checkbox"
                @change="handleCheckAllChange"
              />
            </div>
            <div class="flex w-full flex-1 items-center sm:w-auto">
              <FormKit
                v-if="!selectedMovies.length"
                v-model="searchText"
                placeholder="输入关键词搜索"
                type="text"
                outer-class="!moments-p-0 moments-mr-2"
                @keyup.enter="onKeywordChange"
              >
                <template v-if="keyword" #suffix>
                  <div
                    class="group flex h-full cursor-pointer items-center bg-white px-2 transition-all hover:bg-gray-50"
                    @click="handleReset"
                  >
                    <IconCloseCircle
                      class="h-4 w-4 text-gray-500 group-hover:text-gray-700"
                    />
                  </div>
                </template>
              </FormKit>
              <VSpace v-else v-permission="['plugin:bingewatching:manage']">
                <VButton type="danger" @click="handleDeleteInBatch">
                  删除
                </VButton>
              </VSpace>
            </div>
            <VSpace spacing="lg" class="flex-wrap">
              <FilterCleanButton
                v-if="hasFilters"
                @click="handleClearFilters"
              />
              <FilterDropdown
                v-model="selectedType"
                label="状态"
                :items="movieStatus"
              />
              <FilterDropdown
                v-model="selectedSort"
                label="排序"
                :items="[
                  {
                    label: '默认',
                  },
                  {
                    label: '更新集数从大到小',
                    value: 'spec.newSeen,desc',
                  },
                  {
                    label: '较近创建',
                    value: 'metadata.creationTimestamp,desc',
                  },
                  {
                    label: '较早创建',
                    value: 'metadata.creationTimestamp,asc',
                  },
                ]"
              />
              <div class="flex flex-row gap-2">
                <div
                  class="group cursor-pointer rounded p-1 hover:bg-gray-200"
                  @click="refetch()"
                >
                  <IconRefreshLine
                    v-tooltip="'刷新'"
                    :class="{ 'animate-spin text-gray-900': isFetching }"
                    class="h-4 w-4 text-gray-600 group-hover:text-gray-900"
                  />
                </div>
              </div>
            </VSpace>
          </div>
        </div>
      </template>

      <VLoading v-if="isLoading" />

      <Transition v-else-if="!movies?.items.length" appear name="fade">
        <VEmpty
          title="暂无影视记录"
          message="暂无影视记录"
        >
          <template #actions>
            <VSpace>
              <VButton @click="refetch()">刷新</VButton>
            </VSpace>
          </template>
        </VEmpty>
      </Transition>

      <Transition v-else appear name="fade">
        <div class="w-full relative overflow-x-auto">
          <table class="w-full text-sm text-left text-gray-500 widefat">
            <thead class="text-xs text-gray-700 uppercase bg-gray-50">
              <tr>
                <th
                  v-permission="['plugin:bingewatching:manage']"
                  scope="col"
                  class="px-4 py-3"
                >
                  <div class="w-max flex items-center"></div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">名称</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">海报</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">类型</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">演员</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">评分</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">已看集数</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center gap-1">
                    更新集数
                    <IconParkHelp
                      v-tooltip="'基于更新周期自动计算影视已更新但未看的集数,当你手动更新集数时,会自动清零'"
                      class="h-4 w-4 text-gray-400 cursor-help"
                    />
                  </div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">更新周期</div>
                </th>
                <th scope="col" class="px-4 py-3">
                  <div class="w-max flex items-center">状态</div>
                </th>
                <th
                  v-permission="['plugin:bingewatching:manage']"
                  scope="col"
                  class="px-4 py-3"
                >
                  <div class="w-max flex items-center"></div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="movie in movies.items"
                :key="movie.metadata.name"
                class="border-b last:border-none hover:bg-gray-100"
              >
                <td class="px-4 py-4" v-permission="['plugin:bingewatching:manage']">
                  <input
                    v-model="selectedMovies"
                    :value="movie.metadata.name"
                    class="h-4 w-4 rounded border-gray-300 text-indigo-600"
                    name="movie-checkbox"
                    type="checkbox"
                  />
                </td>
                <td class="px-4 py-4">{{ movie.spec.vodName }}</td>
                <td class="px-4 py-4 poster">
                  <img
                    v-if="movie.spec.vodPic"
                    :src="movie.spec.vodPic"
                    :alt="movie.spec.vodName"
                    referrerpolicy="no-referrer"
                  />
                  <span v-else>-</span>
                </td>
                <td class="px-4 py-4 table-td">
                  {{ movie.spec.typeName || '暂无类型' }}
                </td>
                <td class="px-4 py-4 table-td">
                  {{
                    movie.spec.vodActor && movie.spec.vodActor.length > 10
                      ? movie.spec.vodActor.slice(0, 10) + '...'
                      : movie.spec.vodActor || '暂无演员'
                  }}
                </td>
                <td class="px-4 py-4 table-td">
                  {{ movie.spec.vodScore || '暂无评分' }}
                </td>
                <td class="px-4 py-4 table-td">
                  {{ movie.spec.seen }}
                </td>
                <td class="px-4 py-4 table-td">
                  <span class="text-yellow-likcc">{{ movie.spec.newSeen || '-' }}</span>
                </td>
                <td class="px-4 py-4 table-td">
                  {{ movie.spec.updateCycle }}
                </td>
                <td class="px-4 py-4 table-td">
                  {{ movie.spec.status || '暂无状态' }}
                </td>
                <td class="px-4 py-4 table-td" v-permission="['plugin:bingewatching:manage']">
                  <div class="flex items-center justify-end">
                    <button
                      class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full"
                      @click="handleDecreaseSeen(movie)"
                      v-tooltip="'减少已看集数'"
                    >
                      <IconParkMinus class="h-4 w-4" />
                    </button>
                    <button
                      class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full"
                      @click="handleIncreaseSeen(movie)"
                      v-tooltip="'增加已看集数'"
                    >
                      <IconParkPlus class="h-4 w-4" />
                    </button>
                    <button
                      class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full"
                      @click="handleOpenQuotesModal(movie)"
                      v-tooltip="'经典台词'"
                    >
                      <IconParkMessage class="h-4 w-4" />
                    </button>
                    <button
                      class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full"
                      @click="handleEditMovie(movie)"
                      v-tooltip="'编辑'"
                    >
                      <IconParkEdit class="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </Transition>

      <template #footer>
        <VPagination
          v-model:page="page"
          v-model:size="size"
          :total="total"
          :size-options="[20, 30, 50, 100]"
        />
      </template>
    </VCard>
  </div>
</template>

<style scoped lang="scss">
.widefat * {
  word-wrap: break-word;
}

.widefat td {
  vertical-align: top;
}

.widefat .poster {
  width: 180px;
  img {
    max-width: 100px;
    max-height: 60px;
    object-fit: cover;
    border-radius: 4px;
  }
}

.table-td {
  text-align: left !important;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.text-yellow-likcc {
  color: #1571fa !important;
  font-weight: bold;
}
</style> 

