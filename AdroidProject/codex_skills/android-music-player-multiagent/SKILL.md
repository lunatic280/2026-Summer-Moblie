---
name: android-music-player-multiagent
description: Coordinate multi-agent Android work for this Kotlin Jetpack Compose Spotify-style local music player repository. Use when the user asks to run, create, coordinate, or review the Android Music Player Builder, Compose UI Reviewer, Playback Service Reviewer, or any multi-agent workflow based on spotify_style_music_player_plan.md.
---

# Android Music Player Multiagent

## Overview

Use this skill to coordinate the project-specific agents defined in `.agents/` for the Android music player app. The main agent must still own planning, integration, final verification, and user communication.

## Required Context

Before spawning agents or editing code, read these files from the repository root:

- `AGENTS.md`
- `spotify_style_music_player_plan.md`
- `codex_music_player_agent_setup.md`
- `.agents/android-music-player-builder.md`
- `.agents/compose-ui-reviewer.md`
- `.agents/playback-service-reviewer.md`

If `.agents/` is missing, proceed with the role summaries below and tell the user that project agent files are absent.

## Agent Roles

- `Android Music Player Builder`: worker for bounded implementation milestones. Use for data model, sample catalog, Compose screens, Navigation, ViewModel state, ExoPlayer integration, service, notification, widget, favorites, or recent history.
- `Compose UI Reviewer`: explorer/reviewer for UI structure, accessibility, screen density, Material3 usage, previews, and state hoisting.
- `Playback Service Reviewer`: explorer/reviewer for Media3, `MediaSessionService` or foreground service, notifications, manifest declarations, permissions, receivers, and widget control flow.

## Coordination Rules

- Spawn subagents only when the user explicitly asks for multi-agent work or explicitly invokes this skill.
- Do not delegate the immediate blocking task if the main agent needs it before making progress.
- Assign disjoint ownership when multiple workers edit code.
- Tell every worker that other agents may be editing concurrently and that they must not revert unrelated changes.
- Keep the main agent responsible for applying or integrating returned patches, running final checks, and summarizing the result.
- Do not use agents to add Spotify login, Spotify API streaming, copyrighted downloads, payment flows, or unrelated refactors.

## Workflow

1. Classify the request:
   - Implementation milestone: use one Builder worker, optionally with a reviewer in parallel.
   - UI-only review: use Compose UI Reviewer.
   - Playback/service/widget review: use Playback Service Reviewer.
   - Large milestone with UI and playback risks: use Builder plus the relevant reviewer, with non-overlapping scopes.
2. Read the relevant files locally before delegating.
3. Spawn agents with `multi_agent_v1.spawn_agent`:
   - Use `agent_type: "worker"` for implementation.
   - Use `agent_type: "explorer"` for review or codebase questions.
   - Pass the matching `.agents/*.md` content or path in the prompt.
4. While agents run, do non-overlapping local work.
5. Wait only when their result is needed for the next step.
6. Review returned work, integrate carefully, and run verification.
7. Close completed agents when they are no longer needed.

## Spawn Prompt Templates

Builder:

```text
Use .agents/android-music-player-builder.md. Implement only this milestone: <milestone>.
Owned files/modules: <paths>.
Do not implement Spotify API/login/streaming. Do not touch service/notification/widget unless this milestone explicitly includes it.
You are not alone in the codebase; do not revert unrelated edits. Change files directly and list changed paths plus verification results.
```

Compose reviewer:

```text
Use .agents/compose-ui-reviewer.md. Review the current Compose implementation for: <screens or files>.
Return findings ordered by severity with file references. Do not edit files unless explicitly instructed.
```

Playback reviewer:

```text
Use .agents/playback-service-reviewer.md. Review playback/service/notification/widget integration for: <files or feature>.
Return findings ordered by severity, including manifest and permission concerns. Do not edit files unless explicitly instructed.
```

## Verification

Prefer these checks from the repository root:

- `./gradlew :app:compileDebugKotlin`
- `./gradlew :app:testDebugUnitTest`
- `./gradlew :app:lintDebug`
- `./gradlew :app:connectedDebugAndroidTest` only when a device or emulator is available and Android integration changes require it.
