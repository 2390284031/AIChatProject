<script setup lang="ts">
import type {
  AttachmentsProps,
  BubbleListProps,
  ConversationsProps,
  PromptsProps,
} from 'ant-design-x-vue'
import { onBeforeUnmount, onMounted, type VNode } from 'vue'
import {
  EllipsisOutlined,
  FireOutlined,
  CoffeeOutlined,
  PlusOutlined,
  ShareAltOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import { Badge, Button, Flex, Space, Typography, theme } from 'ant-design-vue'
import {
  Attachments,
  Bubble,
  Conversations,
  Prompts,
  Sender,
  Welcome,
} from 'ant-design-x-vue'
import { computed, h, ref } from 'vue'
import websocket from '@/utils/websocket.ts'
import { VITE_HOST_URL } from '../../env.d.ts'
import { getHots } from '@/api/api.ts'

const { token } = theme.useToken()

const styles = computed(() => {
  return {
    layout: {
      width: '100%',
      'min-width': '1000px',
      height: '722px',
      'border-radius': `${token.value.borderRadius}px`,
      display: 'flex',
      background: `${token.value.colorBgContainer}`,
      'font-family': `AlibabaPuHuiTi, ${token.value.fontFamily}, sans-serif`,
    },
    menu: {
      background: `${token.value.colorBgLayout}80`,
      width: '222px',
      height: '100%',
      display: 'flex',
      'flex-direction': 'column',
    },
    conversations: {
      padding: '0 12px',
      flex: 1,
      'overflow-y': 'auto',
    },
    chat: {
      height: '100%',
      width: '100%',
      'max-width': '700px',
      margin: '0 auto',
      'box-sizing': 'border-box',
      display: 'flex',
      'flex-direction': 'column',
      padding: `${token.value.paddingLG}px`,
      gap: '16px',
    },
    messages: {
      flex: 1,
      whiteSpace: 'pre-wrap'
    },
    placeholder: {
      'padding-top': '32px',
      'text-align': 'left',
      flex: 1,
    },
    sender: {
      'box-shadow': token.value.boxShadow,
    },
    logo: {
      display: 'flex',
      height: '72px',
      'align-items': 'center',
      'justify-content': 'center',
      padding: '0 24px',
      'box-sizing': 'border-box',
    },
    'logo-img': {
      width: '24px',
      height: '24px',
      display: 'inline-block',
    },
    'logo-span': {
      display: 'inline-block',
      margin: '0 8px',
      'font-weight': 'bold',
      color: token.value.colorText,
      'font-size': '16px',
    },
    addBtn: {
      background: '#1677ff0f',
      border: '1px solid #1677ff34',
      width: 'calc(100% - 24px)',
      margin: '0 12px 24px 12px',
    },
    prompts: {
      width: '100%',
      display: 'flex',
      'align-items': 'center',
      'justify-content': 'center',
      'margin-bottom': '10px'
    }
  } as const
})

defineOptions({ name: 'PlaygroundIndependentSetup' })

function renderTitle(icon: VNode, title: string) {
  return h(Space, { align: 'start' }, () => [icon, h('span', title)])
}

const defaultConversationsItems = [
  {
    key: '0',
    label: 'What is Ant Design X?',
  },
]

const placeholderPromptsItems = ref<PromptsProps['items']>([
  {
    key: '1',
    label: renderTitle(h(FireOutlined, { style: { color: '#FF4D4F' } }), '热点话题'),
    description: '你对什么感兴趣？',
    children: [
      {
        key: '1-1',
        description: `科技与人工智能`,
      },
      {
        key: '1-2',
        description: `国际形势`,
      },
      {
        key: '1-3',
        description: `社会文化`,
      },
      {
        key: '1-4',
        description: `"一带一路"十周年`,
      },
      {
        key: '1-5',
        description: `科技与文化`,
      },
      {
        key: '1-6',
        description: `AI发展迅速`,
      },
    ],
  },
])

const senderPromptsItems: PromptsProps['items'] = [
  {
    key: '1',
    description: '热点话题',
    icon: h(FireOutlined, { style: { color: '#FF4D4F' } }),
  },
]
const logoImage = new URL('@/assets/ai_logo.png', import.meta.url).href
const roles: BubbleListProps['roles'] = {
  ai: {
    placement: 'start',
    typing: { step: 5, interval: 20 },
    avatar: { src: logoImage },
    styles: {
      content: {
        borderRadius: '16px',
      },
    },
  },
  user: {
    placement: 'end',
    variant: 'shadow',
    avatar: { icon: h(UserOutlined), style: { background: '#87d068' } },
  },
}

// ==================== State ====================
const headerOpen = ref(false)
const content = ref('')
const conversationsItems = ref(defaultConversationsItems)
const activeKey = ref(defaultConversationsItems[0].key)
const attachedFiles = ref<AttachmentsProps['items']>([])
const agentRequestLoading = ref(false)
const itemList = ref<Item[]>([])

interface Item{
  key: number,
  role: 'user' | 'ai',
  content: string,
  loading: boolean
}

const onPromptsItemClick: PromptsProps['onItemClick'] = (info) => {
  sendMessage(info.data.description as string)
}

function onAddConversation() {
  conversationsItems.value = [
    ...conversationsItems.value,
    {
      key: `${conversationsItems.value.length}`,
      label: `新会话 ${conversationsItems.value.length}`,
    },
  ]
  activeKey.value = `${conversationsItems.value.length}`
}

const onConversationClick: ConversationsProps['onActiveChange'] = (key) => {
  activeKey.value = key
  console.log(activeKey.value)
}

const handleFileChange: AttachmentsProps['onChange'] = (info) =>
  (attachedFiles.value = info.fileList)

// ==================== Nodes ====================

const placeholderNode = computed(() =>
  h(Space, { direction: 'vertical', size: 16, style: styles.value.placeholder }, () => [
    h(Welcome, {
      icon: logoImage,
      title: '你好，我是智能AI',
      description: '我可以帮你写代码、读文件、写作各种创意内容，请把你的任务交给我吧~',
      extra: h(Space, {}, () => [
        h(Button, { icon: h(ShareAltOutlined) }),
        h(Button, { icon: h(EllipsisOutlined) }),
      ]),
    }),
    h(Prompts, {
      title: '想要做什么?',
      items: placeholderPromptsItems.value,
      styles: {
        list: {
          width: '100%',
        },
        item: {
          flex: 1,
        },
      },
      onItemClick: onPromptsItemClick,
    }),
  ]),
)

const PromptsItems: PromptsProps['items'] = [
  {
    key: '1',
    icon: h(CoffeeOutlined, { style: { color: '#964B00' } }),
    label: "AI聊天对话管理",
    description: '功能完善中...',
  },
];

function onSubmit() {
  sendMessage(content.value)
  content.value = ''
}

const items = computed<BubbleListProps['items']>(() => {
  if (itemList.value.length === 0) {
    return [{ content: placeholderNode, variant: 'borderless' }]
  }
  return itemList.value
})

const index = ref()
const sendMessage = (text: string) => {
  if (text.trim()){
    itemList.value.push({
      key: itemList.value.length,
      role: 'user',
      content: text,
      loading: false,
    })
    index.value = itemList.value.length
    itemList.value[index.value] = {
      key: index.value,
      role: 'ai',
      content: msg.value,
      loading: true
    }
    websocket.sendMessage(text.toString(), email)
  }
}
const email = (Math.floor(Math.random() * 1000) + 1).toString()
onMounted(() => {
  websocket.connect("ws://" + VITE_HOST_URL + "/websocket/" + email)
  websocket.handleMessage = handleMessage

  // 获取热点信息
  getHots().then((res) => {
    console.log(res)
    if (res.data.code === 1){
      for (let i = 0; i < 6; i++){
        placeholderPromptsItems.value[0].children[i].description = res.data.data[i]
      }
    }
  })
})

onBeforeUnmount(() => {
  websocket.disconnect()
})

// 收到信息触发
const msg = ref('')
const handleMessage = (data) => {
  const parsedData = JSON.parse(data.toString());
  msg.value = msg.value + parsedData.content
  console.log(msg.value)
  if (parsedData.role === "system"){
    for (let i = 0; i < 6; i++){
      placeholderPromptsItems.value[0].children[i].description = parsedData.content[i]
    }
  } else {
    if (parsedData.content === "[DONE]"){
      agentRequestLoading.value = false
      msg.value = ''
      return
    } else {
      itemList.value[index.value] = {
        key: index.value,
        role: parsedData.role,
        content: msg.value,
        loading: false
      }
      agentRequestLoading.value = true
    }
  }
}

</script>

<template>
  <div :style="styles.layout">
    <div :style="styles.menu">
      <!-- 🌟 Logo -->
      <div :style="styles.logo">
        <img src="@/assets/ai_logo.png" draggable="false" alt="logo" :style="styles['logo-img']" />
        <span :style="styles['logo-span']">AI 聊天对话</span>
      </div>
      <div>
        <Prompts title="" :items="PromptsItems" :style="styles.prompts"/>
      </div>

      <!-- 🌟 添加会话 -->
      <Button type="link" :style="styles.addBtn" @click="onAddConversation">
        <PlusOutlined />
        添加会话
      </Button>

      <!-- 🌟 会话管理 -->
      <Conversations
        :items="conversationsItems"
        :style="styles.conversations"
        :active-key="activeKey"
        @active-change="onConversationClick"
      />
    </div>

    <div :style="styles.chat">
      <!-- 🌟 消息列表 -->
      <Bubble.List :items="items" :roles="roles" :style="styles.messages"/>

      <!-- 🌟 提示词 -->
      <Prompts :items="senderPromptsItems" @item-click="onPromptsItemClick" />

      <!-- 🌟 输入框 -->
      <Sender
        :value="content"
        :style="styles.sender"
        :loading="agentRequestLoading"
        @submit="onSubmit"
        @change="(value) => (content = value)"
      >

<!--        文件上传，开发中....-->
<!--        <template #prefix>-->
<!--          <Badge :dot="attachedFiles.length > 0 && !headerOpen">-->
<!--            <Button type="text" @click="() => (headerOpen = !headerOpen)">-->
<!--              <template #icon>-->
<!--                <PaperClipOutlined />-->
<!--              </template>-->
<!--            </Button>-->
<!--          </Badge>-->
<!--        </template>-->

<!--        <template #header>-->
<!--          <Sender.Header-->
<!--            title="Attachments"-->
<!--            :open="headerOpen"-->
<!--            :styles="{ content: { padding: 0 } }"-->
<!--            @open-change="(open) => (headerOpen = open)"-->
<!--          >-->
<!--            <Attachments-->
<!--              :before-upload="() => false"-->
<!--              :items="attachedFiles"-->
<!--              @change="handleFileChange"-->
<!--            >-->
<!--              <template #placeholder="type">-->
<!--                <Flex-->
<!--                  v-if="type && type.type === 'inline'"-->
<!--                  align="center"-->
<!--                  justify="center"-->
<!--                  vertical-->
<!--                  gap="2"-->
<!--                >-->
<!--                  <Typography.Text style="font-size: 30px; line-height: 1">-->
<!--                    <CloudUploadOutlined />-->
<!--                  </Typography.Text>-->
<!--                  <Typography.Title :level="5" style="margin: 0; font-size: 14px; line-height: 1.5">-->
<!--                    Upload files-->
<!--                  </Typography.Title>-->
<!--                  <Typography.Text type="secondary">-->
<!--                    Click or drag files to this area to upload-->
<!--                  </Typography.Text>-->
<!--                </Flex>-->
<!--                <Typography.Text v-if="type && type.type === 'drop'">-->
<!--                  Drop file here-->
<!--                </Typography.Text>-->
<!--              </template>-->
<!--            </Attachments>-->
<!--          </Sender.Header>-->
<!--        </template>-->
      </Sender>
    </div>
  </div>
</template>
