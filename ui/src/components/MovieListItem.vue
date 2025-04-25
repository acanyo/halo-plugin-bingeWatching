<script setup lang="ts">
import {VEntity, VEntityField, VDropdownItem, Dialog, Toast} from "@halo-dev/components";
import type {HandsomeMovie} from "@/api/generated";
import {handsomeMovieApi} from "@/api";
import {useQueryClient} from "@tanstack/vue-query";

const props = defineProps<{
  movie: HandsomeMovie;
  isSelected?: boolean;
}>();

const queryClient = useQueryClient();

const handleDelete = () => {
  Dialog.warning({
    title: "确定要删除该影视记录吗？",
    description: "删除之后将无法恢复。",
    confirmType: "danger",
    confirmText: "确定",
    cancelText: "取消",
    onConfirm: async () => {
      try {
        await handsomeMovieApi.deleteHandsomeMovie(props.movie.metadata.name);
        Toast.success("删除成功");
      } catch (error) {
        console.error("Failed to delete movie", error);
      } finally {
        queryClient.invalidateQueries({ queryKey: ["movies"] });
      }
    },
  });
};

const handleEdit = () => {
  emit("edit", props.movie);
};

const emit = defineEmits<{
  (e: "edit", movie: HandsomeMovie): void;
}>();
</script>

<template>
  <VEntity :is-selected="isSelected">
    <template #checkbox>
      <slot name="checkbox" />
    </template>
    <template #start>
      <VEntityField :title="movie.spec.vod_name">
        <template #description>
          <div class="flex items-center space-x-2">
            <img
              v-if="movie.spec.vod_pic"
              :src="movie.spec.vod_pic"
              :alt="movie.spec.vod_name"
              class="h-12 w-12 object-cover rounded"
              referrerpolicy="no-referrer"
            />
            <span class="text-sm text-gray-500">{{ movie.spec.type_name || '-' }}</span>
          </div>
        </template>
      </VEntityField>
    </template>
    <template #end>
      <VEntityField>
        <template #description>
          <div class="flex items-center space-x-4">
            <span class="text-sm">已看集数: {{ movie.spec.seen }}</span>
            <span class="text-sm">更新周期: {{ movie.spec.updateCycle }}</span>
            <span class="text-sm">状态: {{ movie.spec.status || '-' }}</span>
          </div>
        </template>
      </VEntityField>
      <VEntityField v-if="movie.metadata.deletionTimestamp">
        <template #description>
          <span class="text-sm text-gray-500">删除中</span>
        </template>
      </VEntityField>
    </template>
    <template #dropdownItems>
      <VDropdownItem @click="handleEdit">
        编辑
      </VDropdownItem>
      <VDropdownItem type="danger" @click="handleDelete">
        删除
      </VDropdownItem>
    </template>
  </VEntity>
</template>

<style scoped>
.table-td {
  text-align: left !important;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 
