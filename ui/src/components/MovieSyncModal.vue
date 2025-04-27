<script lang="ts" setup>
import {Toast, VButton, VModal, VSpace} from "@halo-dev/components";
import {computed, ref, watch} from "vue";
import {FormKit} from "@formkit/vue";
import {handsomeMovieApi} from "@/api";

interface MovieInfo {
  vod_name: string;
  vod_pic: string;
  vod_actor: string;
  vod_lang: string;
  vod_year: string;
  vod_score: string;
  vod_content: string;
  type_name: string;
  id: string;
}

interface FormState {
  vod_name: string;
  updateCycle: string;
  seen: string;
  selectedMovie: MovieInfo | null;
}

const props = withDefaults(
  defineProps<{
    visible: boolean;
  }>(),
  {
    visible: false,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: FormState = {
  vod_name: "",
  updateCycle: "",
  seen: "",
  selectedMovie: null
};

const updateCycleOptions = [
  { label: '周一', value: '1' },
  { label: '周二', value: '2' },
  { label: '周三', value: '3' },
  { label: '周四', value: '4' },
  { label: '周五', value: '5' },
  { label: '周六', value: '6' },
  { label: '周日', value: '7' }
];

const formState = ref<FormState>(initialFormState);
const saving = ref<boolean>(false);
const formVisible = ref(false);
const showMovieSelection = ref(false);
const movieList = ref<MovieInfo[]>([]);
const selectedMovies = ref<Set<string>>(new Set());

const validationMessages = {
  required: (ctx: { name: string }) => `${ctx.name}不能为空`,
  number: () => "请输入正确的数字",
  matches: () => "请输入正确的数字"
} as const;

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

const handleResetForm = () => {
  formState.value = { ...initialFormState };
  showMovieSelection.value = false;
  movieList.value = [];
  selectedMovies.value.clear();
};

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      formVisible.value = true;
    } else {
      setTimeout(() => {
        formVisible.value = false;
        handleResetForm();
      }, 200);
    }
  }
);

const isFormValid = computed(() => {
  if (!formState.value.vod_name?.trim()) return false;
  return true;
});

const handleSelectMovie = (movie: MovieInfo & { id: string }) => {
  const movieId = movie.id;
  if (selectedMovies.value.has(movieId)) {
    selectedMovies.value.delete(movieId);
  } else {
    selectedMovies.value.add(movieId);
  }
};

const handleConfirmSelection = async () => {
  if (selectedMovies.value.size === 0) {
    Toast.warning("请至少选择一部影视");
    return;
  }

  saving.value = true;
  try {
    const selectedMovieList = movieList.value.filter(movie => 
      selectedMovies.value.has(movie.id)
    ).map(movie => ({
      metadata: {
        name: "",
        generateName: "handsomemovie-",
      },
      spec: {
        vod_name: movie.vod_name,
        updateCycle: formState.value.updateCycle,
        seen: formState.value.seen,
        vod_pic: movie.vod_pic || "",
        vod_actor: movie.vod_actor || "",
        vod_lang: movie.vod_lang || "",
        vod_year: movie.vod_year || "",
        vod_score: movie.vod_score || "",
        vod_content: movie.vod_content || "",
        type_name: movie.type_name || "",
        status: "观看中"
      },
      kind: "HandsomeMovie",
      apiVersion: "api.bingewatching.lik.cc/v1alpha1",
    }));

    await handsomeMovieApi.insertMovie(selectedMovieList);
    Toast.success(`成功同步 ${selectedMovies.value.size} 部影视`);
    showMovieSelection.value = false;
    onVisibleChange(false);
  } catch (error) {
    console.error("批量创建影视记录失败:", error);
    Toast.error("同步失败,请检查网络连接是否正常！");
  } finally {
    saving.value = false;
  }
};

const handleSyncMovie = async () => {
  if (!isFormValid.value) {
    if (!formState.value.vod_name?.trim()) {
      Toast.error("影视名称不能为空");
      return;
    }
    Toast.error("请检查表单填写是否正确");
    return;
  }

  try {
    saving.value = true;
    // 先获取影视信息
    const response = await fetch(
      `/apis/api.bingewatching.lik.cc/v1alpha1/movies/-/${encodeURIComponent(formState.value.vod_name)}`
    );
    const result = await response.json();
    
    if (result.error) {
      Toast.error(`获取影视信息失败: ${result.message || '未知错误'}`);
      return;
    }

    // 检查搜索结果数量
    if (result.total < 1) {
      Toast.warning("未找到匹配的影视信息，请检查名称是否正确");
      return;
    }

    // 如果只有一个结果，直接创建
    if (result.total === 1) {
      const movieData = [{
        metadata: {
          name: "",
          generateName: "handsomemovie-",
        },
        spec: {
          vod_name: result.data[0].vod_name,
          updateCycle: formState.value.updateCycle,
          seen: formState.value.seen,
          vod_pic: result.data[0].vod_pic || "",
          vod_actor: result.data[0].vod_actor || "",
          vod_lang: result.data[0].vod_lang || "",
          vod_year: result.data[0].vod_year || "",
          vod_score: result.data[0].vod_score || "",
          vod_content: result.data[0].vod_content || "",
          type_name: result.data[0].type_name || "",
          status: "观看中"
        },
        kind: "HandsomeMovie",
        apiVersion: "api.bingewatching.lik.cc/v1alpha1",
      }];
      
      try {
        await handsomeMovieApi.insertMovie(movieData);
        Toast.success("同步成功");
        onVisibleChange(false);
      } catch (error) {
        console.error("创建影视记录失败:", error);
        Toast.error("同步失败");
      }
    } else {
      // 如果有多个结果，显示选择弹窗
      movieList.value = result.data;
      showMovieSelection.value = true;
    }
  } catch (error) {
    console.error("获取影视信息失败:", error);
    Toast.error("同步失败");
  } finally {
    saving.value = false;
  }
};
</script>

<template>
  <VModal
    title="获取影视信息"
    :visible="visible"
    :width="500"
    @update:visible="onVisibleChange"
  >
    <FormKit
      v-if="formVisible"
      id="movie-sync-form"
      name="movie-sync-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
    >
      <div class="likcc-form-container">
        <div class="likcc-form-content">
          <!-- 影视名称输入框 -->
          <FormKit
            v-model="formState.vod_name"
            type="text"
            name="vod_name"
            validation="required"
            :validation-messages="validationMessages"
            label="影视名称"
            placeholder="请输入要同步的影视剧名称，系统将自动搜索并同步相关信息"
            help="输入准确的影视剧名称可以提高搜索成功率 如《不良人7》"
          />

          <!-- 更新周期选择 -->
          <FormKit
            v-model="formState.updateCycle"
            type="select"
            name="updateCycle"
            label="更新周期"
            :options="updateCycleOptions"
            placeholder="请选择更新周期"
            help="选择正确的更新周期有助于追踪最新剧集"
          />

          <!-- 已看集数输入 -->
          <FormKit
            v-model="formState.seen"
            type="number"
            name="seen"
            label="已看集数"
            validation="number|matches:/^[0-9]+$/"
            :validation-messages="validationMessages"
            placeholder="请输入已观看集数 比如 12"
            help="可以输入具体集数，如'12' 必须是纯数字"
            min="0"
          />
        </div>
      </div>
    </FormKit>

    <template #footer>
      <VSpace>
        <VButton type="secondary" @click="onVisibleChange(false)">
          取消
        </VButton>
        <VButton
          :loading="saving"
          type="primary"
          :disabled="!isFormValid"
          @click="handleSyncMovie"
        >
          确定
        </VButton>
      </VSpace>
    </template>
  </VModal>

  <!-- 影视选择弹窗 -->
  <VModal
    title="请选择影视"
    :visible="showMovieSelection"
    :width="1000"
    @update:visible="(v) => showMovieSelection = v"
    class="movie-selection-modal"
  >
    <div class="likcc-movie-grid">
      <div
        v-for="movie in movieList"
        :key="movie.id"
        :class="['likcc-movie-item', { 'selected': selectedMovies.has(movie.id) }]"
        @click="handleSelectMovie(movie)"
      >
        <div class="likcc-movie-poster">
          <img
            :src="movie.vod_pic"
            :alt="movie.vod_name"
            loading="lazy"
          >
        </div>
        <div class="likcc-movie-title">{{ movie.vod_name }}</div>
      </div>
    </div>

    <template #footer>
      <div class="likcc-modal-footer">
        <span class="likcc-selection-count" v-if="selectedMovies.size > 0">
          已选择 {{ selectedMovies.size }} 部影视
        </span>
        <VSpace>
          <VButton type="secondary" @click="showMovieSelection = false">
            取消
          </VButton>
          <VButton
            type="primary"
            :loading="saving"
            :disabled="selectedMovies.size === 0"
            @click="handleConfirmSelection"
          >
            确定
          </VButton>
        </VSpace>
      </div>
    </template>
  </VModal>
</template>

<style scoped lang="scss">
.likcc-form-container {
  @apply px-3;
}

.likcc-form-content {
  @apply space-y-4;

  :deep(.formkit-outer) {
    @apply bg-white rounded-lg p-3 border border-gray-100;
  }

  :deep(.formkit-label) {
    @apply text-sm font-medium text-gray-900 mb-1;
  }

  :deep(.formkit-help) {
    @apply text-xs text-gray-600 mt-1;
  }

  :deep(.formkit-description) {
    @apply text-xs text-gray-500 mt-1;
  }

  :deep(.formkit-input) {
    @apply mt-1 block w-full rounded-md border-gray-300 shadow-sm 
    focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm;
    height: 32px;
  }

  :deep(.formkit-select) {
    @apply mt-1 block w-full rounded-md border-gray-300 shadow-sm 
    focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm;
    height: 32px;
  }
}

.likcc-movie-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  padding: 16px;
  background: #f5f5f5;
  max-height: 85vh;
  overflow-y: auto;
}

.likcc-movie-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }

  &.selected .likcc-movie-poster {
    border-color: #ff4d4f;
    border-width: 4px;
    box-shadow: 0 0 0 2px #ff4d4f;
  }
}

.likcc-movie-poster {
  width: 160px;
  height: 220px;
  border: 3px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: all 0.2s;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.likcc-movie-title {
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  text-align: center;
  width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.likcc-modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.likcc-selection-count {
  font-size: 14px;
  color: #666;
}

.movie-selection-modal {
  :deep(.modal-container) {
    max-width: 1000px;
    width: 90vw;
  }
}
</style> 
