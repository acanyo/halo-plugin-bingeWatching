<script lang="ts" setup>
import { Toast, VButton, VModal } from "@halo-dev/components";
import { ref, watch, computed } from "vue";
import { handsomeMovieApi } from "@/api";
import type { HandsomeMovie } from "@/api/generated";

const props = defineProps<{
  visible: boolean;
  movie?: HandsomeMovie;
}>();

const emit = defineEmits<{
  (e: "update:visible", visible: boolean): void;
  (e: "close"): void;
}>();

const classicLines = ref<string[]>([]);
const loading = ref(false);

const isVisible = computed({
  get: () => props.visible,
  set: (value) => emit("update:visible", value)
});

watch(
  () => props.movie,
  (movie) => {
    if (movie) {
      classicLines.value = movie.spec.classicLines || [];
    }
  },
  { immediate: true }
);

const handleClose = () => {
  isVisible.value = false;
  emit("close");
};

const handleAddLine = () => {
  if (classicLines.value.length < 10) {
    classicLines.value.push("");
  }
};

const handleRemoveLine = (index: number) => {
  classicLines.value.splice(index, 1);
};

const handleSave = async () => {
  if (!props.movie) return;
  
  try {
    loading.value = true;
    const updatedMovie = { ...props.movie };
    updatedMovie.spec.classicLines = classicLines.value.filter(line => line.trim() !== "");
    await handsomeMovieApi.updateHandsomeMovie(updatedMovie.metadata.name, updatedMovie);
    Toast.success("保存成功");
    handleClose();
  } catch (error) {
    console.error("保存经典台词失败:", error);
    Toast.error("保存失败");
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <VModal
    v-model:visible="isVisible"
    :title="'经典台词'"
    :width="600"
    @close="handleClose"
  >
    <div class="p-4">
      <FormKit
        type="form"
        :actions="false"
        @submit="handleSave"
      >
        <div class="space-y-4">
          <div class="flex justify-between items-center">
            <label class="text-sm font-medium text-gray-700">经典台词</label>
            <VButton
              type="secondary"
              size="sm"
              @click="handleAddLine"
              :disabled="classicLines.length >= 10"
            >
              添加台词
            </VButton>
          </div>
          <div class="space-y-2">
            <div
              v-for="(line, index) in classicLines"
              :key="index"
              class="flex items-center gap-2"
            >
              <FormKit
                type="text"
                v-model="classicLines[index]"
                :name="`classicLines.${index}`"
                :label="`台词 ${index + 1}`"
                validation="required"
                validation-label="台词内容"
              />
              <button
                type="button"
                class="p-1 text-gray-500 hover:text-gray-700"
                @click="handleRemoveLine(index)"
              >
                <IconParkClose class="h-4 w-4" />
              </button>
            </div>
          </div>
          <p class="text-sm text-gray-500">最多添加10条经典台词</p>
        </div>

        <div class="flex justify-end gap-2 mt-4">
          <VButton
            type="secondary"
            @click="handleClose"
          >
            取消
          </VButton>
          <VButton
            type="primary"
            :loading="loading"
            @click="handleSave"
          >
            保存
          </VButton>
        </div>
      </FormKit>
    </div>
  </VModal>
</template> 