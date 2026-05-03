# 当前用户菜单模块对接反馈

## 背景

前端将按 `GET /api/auth/menus` 驱动侧边栏业务菜单，同时继续保留本地默认 Dashboard 和 Demo 菜单。

## 确认结果

- 后端菜单 `path` 保证和当前前端静态路由完全一致，例如 `/system/user`、`/system/dept`、`/system/dict`、`/system/config`、`/system/file`。前端可按 `path` 匹配已注册页面。
- 后端不返回 Dashboard 或 Demo 菜单。前端继续使用本地硬编码默认菜单。
- `menuType=LINK` 的外链需要在第一版侧边栏展示，前端按新窗口打开处理。
- `menuType=DIR` 有 `path` 但没有任何 `visible=1` 子菜单时，前端不展示该空目录。

## 前端实现建议

- 仅渲染 `visible=1` 的菜单项。
- 同级菜单按 `sortOrder` 升序排序。
- `MENU` 使用 `path` 进行内部路由跳转。
- `LINK` 使用 `externalUrl` 并通过新窗口打开。
- `DIR` 在过滤后没有可见子菜单时不展示。
