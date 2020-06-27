## Sample todo iOS app

Before opening the Xcode project please run the following gradle tasks:
- `:sample:todo-darwin-umbrella:iosX64MainBinaries`
- `:sample:todo-darwin-umbrella:iosArm64MainBinaries`

### Debugging in Xcode

Kotlin sources are exported to Xcode via [KotlinXcodeSync](https://github.com/touchlab/KotlinXcodeSync) plugin.
It is enabled only for certain modules.
If you want to use Xcode debugger with Kotlin you can use [xcode-kotlin](https://github.com/touchlab/xcode-kotlin) plugin for Xcode.
