module.exports = {
    extends: ['@commitlint/config-conventional'],
    rules: {
        // 类型枚举
        'type-enum': [
            2,
            'always',
            [
                'feat',     // 新功能
                'fix',      // 修复
                'docs',     // 文档
                'style',    // 格式（不影响代码运行的变动）
                'refactor', // 重构
                'perf',     // 性能优化
                'test',     // 测试
                'chore',    // 构建过程或辅助工具的变动
                'revert',   // 回滚
                'build',    // 构建
                'ci',       // CI/CD
            ],
        ],
        // 类型必须小写
        'type-case': [2, 'always', 'lower-case'],
        // 类型不能为空
        'type-empty': [2, 'never'],
        // 范围可以为空
        'scope-empty': [0],
        // 描述不能为空
        'subject-empty': [2, 'never'],
        // 描述长度限制
        'subject-max-length': [2, 'always', 100],
        // 描述必须以字母开头
        'subject-full-stop': [2, 'never', '.'],
        // 头部最大长度
        'header-max-length': [2, 'always', 100],
    },
    // 提示信息
    prompt: {
        messages: {
            skip: '回车跳过',
            max: '最多%d个字符',
            min: '至少%d个字符',
            emptyWarning: '不能为空',
            upperLimitWarning: '超过长度限制',
            lowerLimitWarning: '低于最小长度',
        },
        questions: {
            type: {
                description: '选择你要提交的类型:',
                enum: {
                    feat: {
                        description: '新功能',
                        title: 'Features',
                        emoji: '✨',
                    },
                    fix: {
                        description: '修复',
                        title: 'Bug Fixes',
                        emoji: '🐛',
                    },
                    docs: {
                        description: '文档',
                        title: 'Documentation',
                        emoji: '📚',
                    },
                    style: {
                        description: '格式（不影响代码运行的变动）',
                        title: 'Styles',
                        emoji: '💎',
                    },
                    refactor: {
                        description: '重构',
                        title: 'Code Refactoring',
                        emoji: '📦',
                    },
                    perf: {
                        description: '性能优化',
                        title: 'Performance Improvements',
                        emoji: '🚀',
                    },
                    test: {
                        description: '测试',
                        title: 'Tests',
                        emoji: '🚨',
                    },
                    chore: {
                        description: '构建过程或辅助工具的变动',
                        title: 'Chores',
                        emoji: '♻️',
                    },
                },
            },
            scope: {
                description: '选择修改的范围（模块）:',
            },
            subject: {
                description: '简短描述（建议不超过50字）:',
            },
            body: {
                description: '详细描述（可选）:',
            },
            footer: {
                description: '关联的Issue（可选，例如: #123）:',
            },
        },
    },
};
