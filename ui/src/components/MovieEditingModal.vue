<script lang="ts" setup>
import {Toast, VButton, VModal, VSpace} from "@halo-dev/components";
import {computed, ref, watch} from "vue";
import {cloneDeep} from 'lodash';
import {handsomeMovieApi} from "@/api";
import type {HandsomeMovie} from "@/api/generated";
import {FormKit} from "@formkit/vue";

interface StatusOption {
  label: string;
  value: string;
}

const props = withDefaults(
  defineProps<{
    visible: boolean;
    movie?: HandsomeMovie;
  }>(),
  {
    visible: false,
    movie: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: HandsomeMovie = {
  metadata: {
    name: "",
    generateName: "handsomemovie-",
  },
  spec: {
    vod_name: "",
    vod_en: "",
    vod_pic: "",
    vod_actor: "",
    vod_lang: "",
    vod_year: "",
    vod_score: "",
    vod_content: "",
    type_name: "",
    seen: "",
    updateCycle: "",
    status: ""
  },
  kind: "HandsomeMovie",
  apiVersion: "bingewatching.lik.cc/v1alpha1",
};

const formState = ref<HandsomeMovie>(cloneDeep(initialFormState));
const saving = ref<boolean>(false);
const formVisible = ref(false);
const movieStatus = ref<StatusOption[]>([
  { label: '观看中', value: '观看中' },
  { label: '完结', value: '完结' },
  { label: '弃坑', value: '弃坑' }
]);

const validationMessages = {
  required: (ctx: { name: string }) => `${ctx.name}不能为空`,
} as const;

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑影视" : "新建影视";
});

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

const handleResetForm = () => {
  formState.value = cloneDeep(initialFormState);
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

watch(
  () => props.movie,
  (movie) => {
    if (movie) {
      formState.value = cloneDeep(movie);
    }
  },
  {
    immediate: true,
  }
);

const isFormValid = computed(() => {
  if (!formState.value.spec.vod_name?.trim()) return false;
  if (!formState.value.spec.vod_pic?.trim()) return false;
  return true;
});

const handleSaveMovie = async () => {
  try {
    if (!isFormValid.value) {
      if (!formState.value.spec.vod_name?.trim()) {
        Toast.error("影视名称不能为空");
        return;
      }
      if (!formState.value.spec.vod_pic?.trim()) {
        Toast.error("影视图片不能为空");
        return;
      }
      Toast.error("请检查表单填写是否正确");
      return;
    }

    saving.value = true;
    
    // 检查已看集数是否变更
    if (props.movie && formState.value.spec.seen !== props.movie.spec.seen) {
      formState.value.spec.newSeen = "0";
    }

    if (isUpdateMode.value) {
      await handsomeMovieApi.updateHandsomeMovie(
        formState.value.metadata.name,
        formState.value
      );
    } else {
      await handsomeMovieApi.createHandsomeMovie(formState.value);
    }

    Toast.success("保存成功");
    onVisibleChange(false);
  } catch (e) {
    console.error(e);
    Toast.error("保存失败");
  } finally {
    saving.value = false;
  }
};
</script>

<template>
  <VModal
    :title="modalTitle"
    :visible="visible"
    :width="650"
    @update:visible="onVisibleChange"
  >
    <template #actions>
      <slot name="append-actions" />
    </template>

    <FormKit
      v-if="formVisible"
      id="movie-form"
      name="movie-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
      @submit="handleSaveMovie"
    >
      <div class="md:grid md:grid-cols-4 md:gap-6">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base font-medium text-gray-900"> 基本信息 </span>
          </div>
        </div>
        <div class="mt-5 divide-y divide-gray-100 md:col-span-3 md:mt-0">
          <FormKit
            v-model="formState.spec.vod_name"
            type="text"
            name="vod_name"
            validation="required"
            :validation-messages="validationMessages"
            label="影视名称"
          ></FormKit>
          <FormKit
            v-model="formState.spec.vod_en"
            type="text"
            name="vod_en"
            label="英文名称"
          ></FormKit>
          <FormKit
            type="attachment"
            v-model="formState.spec.vod_pic"
            name="vod_pic"
            label="影视图片"
            validation="required"
            :validation-messages="validationMessages"
          ></FormKit>
          <FormKit
            v-model="formState.spec.vod_actor"
            type="text"
            name="vod_actor"
            label="影视演员"
          ></FormKit>
          <FormKit
            v-model="formState.spec.vod_lang"
            type="text"
            name="vod_lang"
            label="地区语言"
          ></FormKit>
          <FormKit
            v-model="formState.spec.vod_year"
            type="text"
            name="vod_year"
            label="影视年份"
          ></FormKit>
          <FormKit
            v-model="formState.spec.vod_score"
            type="text"
            name="vod_score"
            label="影视评分"
          ></FormKit>
          <FormKit
            v-model="formState.spec.vod_content"
            type="textarea"
            name="vod_content"
            label="影视描述"
            :rows="3"
          ></FormKit>
          <FormKit
            v-model="formState.spec.type_name"
            type="text"
            name="type_name"
            label="影视类型"
          ></FormKit>
          <FormKit
            v-model="formState.spec.seen"
            type="text"
            name="seen"
            label="已看集数"
          ></FormKit>
          <FormKit
            v-model="formState.spec.updateCycle"
            type="text"
            name="updateCycle"
            label="更新周期/周"
          ></FormKit>
          <FormKit
            :options="movieStatus"
            label="追剧状态"
            v-model="formState.spec.status"
            name="status"
            type="select"
            :value="formState.spec.status || ''"
          ></FormKit>
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
          @click="handleSaveMovie"
        >
          确定
        </VButton>
      </VSpace>
    </template>
  </VModal>
</template>

<style scoped lang="scss">
.divide-y {
  margin-bottom: 9px;
  line-height: 1.3;
  padding-bottom: 1rem;
}
</style> 
