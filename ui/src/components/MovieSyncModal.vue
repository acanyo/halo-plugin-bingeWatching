<script lang="ts" setup>
import { Toast, VButton, VModal, VSpace } from "@halo-dev/components";
import { computed, ref, watch } from "vue";
import { FormKit } from "@formkit/vue";
import { handsomeMovieApi } from "@/api";
import type { HandsomeMovie } from "@/api/generated";

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

const initialFormState = {
  vod_name: "",
  updateCycle: "",
  seen: "",
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

const formState = ref(initialFormState);
const saving = ref<boolean>(false);
const formVisible = ref(false);

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
  if (!formState.value.updateCycle?.trim()) return false;
  if (!formState.value.seen?.trim()) return false;
  return true;
});

const handleSyncMovie = async () => {
  try {
    if (!isFormValid.value) {
      if (!formState.value.vod_name?.trim()) {
        Toast.error("影视名称不能为空");
        return;
      }
      if (!formState.value.updateCycle?.trim()) {
        Toast.error("更新周期不能为空");
        return;
      }
      if (!formState.value.seen?.trim()) {
        Toast.error("已看集数不能为空");
        return;
      }
      Toast.error("请检查表单填写是否正确");
      return;
    }

    saving.value = true;
    
    try {
      // 先获取影视信息
      const response = await fetch(
        `/apis/api.bingewatching.lik.cc/v1alpha1/movies/-/${encodeURIComponent(formState.value.vod_name)}`
      );
      
      if (!response.ok) {
        throw new Error(`获取影视信息失败: ${response.status}`);
      }
      
      const movieInfo = await response.json();
      
      // 合并用户输入和获取的影视信息
      const movieData: HandsomeMovie = {
        metadata: {
          name: "",
          generateName: "handsomemovie-",
        },
        spec: {
          vod_name: movieInfo.vod_name || formState.value.vod_name,
          updateCycle: formState.value.updateCycle,
          seen: formState.value.seen,
          vod_pic: movieInfo.vod_pic || "",
          vod_actor: movieInfo.vod_actor || "",
          vod_lang: movieInfo.vod_lang || "",
          vod_year: movieInfo.vod_year || "",
          vod_score: movieInfo.vod_score || "",
          vod_content: movieInfo.vod_content || "",
          type_name: movieInfo.type_name || "",
          status: "想看"
        },
        kind: "HandsomeMovie",
        apiVersion: "bingewatching.lik.cc/v1alpha1",
      };

      await handsomeMovieApi.createHandsomeMovie(movieData);
      Toast.success("同步成功");
      onVisibleChange(false);
    } catch (error) {
      console.error("获取影视信息失败:", error);
      Toast.error("获取影视信息失败，请检查影视名称是否正确");
    }
  } catch (e) {
    console.error(e);
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
    <template #actions>
      <slot name="append-actions" />
    </template>

    <FormKit
      v-if="formVisible"
      id="movie-sync-form"
      name="movie-sync-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
      @submit="handleSyncMovie"
    >
      <div class="likcc-form-container">
        <div class="likcc-form-content">
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

          <FormKit
            v-model="formState.updateCycle"
            type="select"
            name="updateCycle"
            label="更新周期"
            validation="required"
            :validation-messages="validationMessages"
            :options="updateCycleOptions"
            placeholder="请选择更新周期"
            help="选择正确的更新周期有助于追踪最新剧集"
          />

          <FormKit
            v-model="formState.seen"
            type="number"
            name="seen"
            label="已看集数"
            validation="required|number|matches:/^[0-9]+$/"
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
</style> 